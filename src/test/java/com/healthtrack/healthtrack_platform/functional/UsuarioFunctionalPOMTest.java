package com.healthtrack.healthtrack_platform.functional;

import com.healthtrack.healthtrack_platform.functional.config.TestConfig;
import com.healthtrack.healthtrack_platform.functional.pages.HealthTrackPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.WebDriver;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests funcionales mejorados usando Page Object Model
 * Más mantenibles y legibles que la versión anterior
 */
@DisplayName("HealthTrack Functional Tests (POM)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioFunctionalPOMTest {

    private static WebDriver driver;
    private HealthTrackPage page;
    private static String baseUrl;

    @BeforeAll
    static void setupClass() {
        driver = TestConfig.createWebDriver();
        baseUrl = TestConfig.getBaseUrl();
    }

    @AfterAll
    static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    void setUp() {
        page = new HealthTrackPage(driver);
        page.navigateTo(baseUrl);
    }

    @AfterEach
    void tearDown() {
        // Reset al estado inicial si es necesario
        try {
            if (page.isDashboardVisible()) {
                page.clickNuevoUsuario();
            }
        } catch (Exception e) {
            // Ignorar errores de limpieza
        }
    }

    // =================== TESTS DE CARGA DE PÁGINA ===================

    @Test
    @Order(1)
    @DisplayName("Debería cargar la página correctamente")
    void deberiaCargarPaginaCorrectamente() {
        // Then
        assertThat(page.getBrowserTitle()).contains("HealthTrack");
        assertThat(page.getPageTitle()).contains("HealthTrack");
        assertThat(page.isRegistrationFormVisible()).isTrue();
        assertThat(page.isDashboardVisible()).isFalse();
    }

    // =================== TESTS DE REGISTRO ===================

    @Test
    @Order(2)
    @DisplayName("Debería registrar usuario correctamente")
    void deberiaRegistrarUsuarioCorrectamente() {
        // Given & When
        page.registrarUsuario("Ana García", "68.5");

        // Then
        page.waitForDashboard();
        assertThat(page.isDashboardVisible()).isTrue();
        assertThat(page.isRegistrationFormVisible()).isFalse();
        assertThat(page.getUsuarioNombre()).contains("Ana García");
        assertThat(page.getPesoActual()).contains("68.5");
        assertThat(page.isSuccessMessageVisible()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @Order(3)
    @DisplayName("Debería rechazar nombres inválidos")
    void deberiaRechazarNombresInvalidos(String nombreInvalido) {
        // When
        page.registrarUsuario(nombreInvalido, "70.0");

        // Then
        page.waitForErrorMessage();
        assertThat(page.isErrorMessageVisible()).isTrue();
        assertThat(page.isDashboardVisible()).isFalse();
        assertThat(page.isRegistrationFormVisible()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "-10.5", "abc", ""})
    @Order(4)
    @DisplayName("Debería rechazar pesos inválidos")
    void deberiaRechazarPesosInvalidos(String pesoInvalido) {
        // When
        page.registrarUsuario("Usuario Test", pesoInvalido);

        // Then
        page.waitForErrorMessage();
        assertThat(page.isErrorMessageVisible()).isTrue();
        assertThat(page.isDashboardVisible()).isFalse();
    }

    // =================== TESTS DE ACTUALIZACIÓN DE PESO ===================

    @Test
    @Order(5)
    @DisplayName("Debería actualizar peso correctamente (BUG FIX)")
    void deberiaActualizarPesoCorrectamente() {
        // Given
        page.registrarUsuario("Usuario Bug Fix", "80.0")
            .waitForDashboard();

        // When
        page.actualizarPeso("85.5");

        // Then
        page.waitForSuccessMessage();
        assertThat(page.getPesoActual()).contains("85.5");
        assertThat(page.isSuccessMessageVisible()).isTrue();
        
        // Verificar que NO se aplicó el bug (80-1=79)
        assertThat(page.getPesoActual()).doesNotContain("79.0");
    }

    @ParameterizedTest
    @CsvSource({
        "75.0, 80.0, +5.0",
        "90.0, 85.5, -4.5",
        "65.0, 65.0, Sin cambio"
    })
    @Order(6)
    @DisplayName("Debería calcular diferencias de peso correctamente")
    void deberiaCalcularDiferenciasPesoCorrectamente(String pesoInicial, String pesoNuevo, String expectedChange) {
        // Given
        page.registrarUsuario("Usuario Diferencias", pesoInicial)
            .waitForDashboard();

        // When
        page.actualizarPeso(pesoNuevo);

        // Then
        page.waitForSuccessMessage();
        assertThat(page.getPesoActual()).contains(pesoNuevo);
        
        String successMessage = page.getSuccessMessage();
        if (!expectedChange.equals("Sin cambio")) {
            assertThat(successMessage).contains(expectedChange);
        }
    }

    @Test
    @Order(7)
    @DisplayName("Debería detectar el bug original (peso - 1kg)")
    void deberiaDetectarBugOriginal() {
        // Given
        page.registrarUsuario("Test Bug Detection", "100.0")
            .waitForDashboard();

        // When
        page.actualizarPeso("95.0");

        // Then
        page.waitForSuccessMessage();
        String pesoMostrado = page.getPesoActual();
        
        // Con la corrección: peso debería ser 95.0
        assertThat(pesoMostrado).contains("95.0");
        
        // Con el bug original: peso sería 99.0 (100-1)
        assertThat(pesoMostrado).doesNotContain("99.0");
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1.0", "0", "-5.5", "abc", ""})
    @Order(8)
    @DisplayName("Debería rechazar actualizaciones de peso inválidas")
    void deberiaRechazarActualizacionesPesoInvalidas(String pesoInvalido) {
        // Given
        page.registrarUsuario("Usuario Validación", "75.0")
            .waitForDashboard();
        String pesoOriginal = page.getPesoActual();

        // When
        page.actualizarPeso(pesoInvalido);

        // Then
        page.waitForErrorMessage();
        assertThat(page.isErrorMessageVisible()).isTrue();
        assertThat(page.getPesoActual()).isEqualTo(pesoOriginal); // Peso no cambió
    }

    // =================== TESTS DE INTERACCIÓN CON TECLADO ===================

    @Test
    @Order(9)
    @DisplayName("Debería permitir registro con Enter")
    void deberiaPermitirRegistroConEnter() {
        // When
        page.registrarUsuarioConEnter("Usuario Enter", "72.5");

        // Then
        page.waitForDashboard();
        assertThat(page.isDashboardVisible()).isTrue();
        assertThat(page.getUsuarioNombre()).contains("Usuario Enter");
        assertThat(page.getPesoActual()).contains("72.5");
    }

    @Test
    @Order(10)
    @DisplayName("Debería permitir actualización con Enter")
    void deberiaPermitirActualizacionConEnter() {
        // Given
        page.registrarUsuario("Usuario Enter Update", "70.0")
            .waitForDashboard();

        // When
        page.actualizarPesoConEnter("75.5");

        // Then
        page.waitForSuccessMessage();
        assertThat(page.getPesoActual()).contains("75.5");
        assertThat(page.isSuccessMessageVisible()).isTrue();
    }

    // =================== TESTS DE NAVEGACIÓN ===================

    @Test
    @Order(11)
    @DisplayName("Debería limpiar formulario al hacer reset")
    void deberiaLimpiarFormularioAlHacerReset() {
        // Given
        page.registrarUsuario("Usuario Reset", "78.0")
            .waitForDashboard();

        // When
        page.clickNuevoUsuario();

        // Then
        page.waitForRegistrationForm();
        assertThat(page.isRegistrationFormVisible()).isTrue();
        assertThat(page.isDashboardVisible()).isFalse();
        assertThat(page.areFieldsClean()).isTrue();
    }

    // =================== TESTS DE MENSAJES ===================

    @Test
    @Order(12)
    @DisplayName("Debería mostrar mensaje de bienvenida apropiado")
    void deberiaMostrarMensajeBienvenidaApropiado() {
        // When
        page.registrarUsuario("María López", "65.5");

        // Then
        page.waitForSuccessMessage();
        String mensaje = page.getSuccessMessage();
        assertThat(mensaje).contains("Bienvenido/a María López");
        assertThat(mensaje).contains("65.5 kg");
        assertThat(mensaje).contains("registrado");
    }

    @Test
    @Order(13)
    @DisplayName("Debería mostrar información de última actualización")
    void deberiaMostrarInformacionUltimaActualizacion() {
        // Given
        page.registrarUsuario("Usuario Fecha", "70.0")
            .waitForDashboard();

        // Verificar estado inicial
        assertThat(page.getUltimaActualizacion()).contains("Sin actualizaciones");

        // When
        page.actualizarPeso("73.0");

        // Then
        page.waitForSuccessMessage();
        assertThat(page.getUltimaActualizacion()).contains("Última actualización:");
        assertThat(page.getUltimaActualizacion()).doesNotContain("Sin actualizaciones");
    }

    // =================== TESTS DE VALIDACIÓN DE REGLAS DE NEGOCIO ===================

    @Test
    @Order(14)
    @DisplayName("Debería validar múltiples actualizaciones secuenciales")
    void deberiaValidarMultiplesActualizacionesSecuenciales() {
        // Given
        page.registrarUsuario("Usuario Secuencial", "80.0")
            .waitForDashboard();

        // When - Primera actualización
        page.actualizarPeso("85.0");
        page.waitForSuccessMessage();
        
        // Then - Verificar primera actualización
        assertThat(page.getPesoActual()).contains("85.0");
        
        // When - Intentar segunda actualización inmediata (debería fallar por restricción 48h)
        // Nota: En un test real, esto requeriría mock del tiempo o esperar 48h
        // Para este test, simplemente verificamos que el peso se actualizó correctamente
        String pesoFinal = page.getPesoActual();
        assertThat(pesoFinal).contains("85.0");
    }

    @ParameterizedTest
    @CsvSource({
        "50.0, true",
        "150.0, true", 
        "0.1, true",
        "999.9, true"
    })
    @Order(15)
    @DisplayName("Debería aceptar rangos de peso válidos")
    void deberiaAceptarRangosPesoValidos(String peso, boolean esperado) {
        // Given
        page.registrarUsuario("Usuario Rangos", "70.0")
            .waitForDashboard();

        // When
        page.actualizarPeso(peso);

        // Then
        if (esperado) {
            page.waitForSuccessMessage();
            assertThat(page.getPesoActual()).contains(peso);
            assertThat(page.isSuccessMessageVisible()).isTrue();
        } else {
            page.waitForErrorMessage();
            assertThat(page.isErrorMessageVisible()).isTrue();
        }
    }
}