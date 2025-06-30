package com.healthtrack.healthtrack_platform.functional;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.io.File;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests funcionales para la plataforma HealthTrack usando Selenium
 * Simula el comportamiento real del usuario en la interfaz web
 */
@DisplayName("HealthTrack Functional Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UsuarioFunctionalTest {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static String baseUrl;

    // Selectores CSS para elementos de la página
    private static final String NOMBRE_INPUT = "#nombre";
    private static final String PESO_INICIAL_INPUT = "#peso-inicial";
    private static final String BTN_REGISTRAR = "#btn-registrar";
    private static final String NUEVO_PESO_INPUT = "#nuevo-peso";
    private static final String BTN_ACTUALIZAR = "#btn-actualizar";
    private static final String BTN_NUEVO_USUARIO = "#btn-nuevo-usuario";
    private static final String USUARIO_NOMBRE = "#usuario-nombre";
    private static final String PESO_ACTUAL = "#peso-actual";
    private static final String ULTIMA_ACTUALIZACION = "#ultima-actualizacion";
    private static final String ERROR_MESSAGE = "#error-message";
    private static final String SUCCESS_MESSAGE = "#success-message";
    private static final String WARNING_MESSAGE = "#warning-message";
    private static final String REGISTRATION_FORM = "#registration-form";
    private static final String USER_DASHBOARD = "#user-dashboard";

    @BeforeAll
    static void setupClass() {
        // Configurar WebDriverManager para gestión automática de drivers
        WebDriverManager.chromedriver().setup();
        
        // Configurar opciones de Chrome para tests
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Ejecutar sin interfaz gráfica en CI
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        
        // Configurar URL base (archivo local)
        File htmlFile = new File("src/test/resources/index.html");
        if (htmlFile.exists()) {
            baseUrl = "file://" + htmlFile.getAbsolutePath();
        } else {
            // Fallback para estructura de proyecto diferente
            baseUrl = "file://" + System.getProperty("user.dir") + "/index.html";
        }
    }

    @AfterAll
    static void tearDownClass() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    void setUp() {
        driver.get(baseUrl);
        waitForPageLoad();
    }

    @AfterEach
    void tearDown() {
        // Limpiar estado entre tests si es necesario
        try {
            if (isElementVisible(BTN_NUEVO_USUARIO)) {
                driver.findElement(By.cssSelector(BTN_NUEVO_USUARIO)).click();
                waitForElement(REGISTRATION_FORM);
            }
        } catch (Exception e) {
            // Ignorar errores de limpieza
        }
    }

    // =================== TESTS DE REGISTRO ===================

    @Test
    @Order(1)
    @DisplayName("Debería cargar la página principal correctamente")
    void deberiaCargarPaginaPrincipalCorrectamente() {
        // Then
        assertThat(driver.getTitle()).contains("HealthTrack");
        assertThat(driver.findElement(By.tagName("h1")).getText()).contains("HealthTrack");
        assertThat(isElementVisible(REGISTRATION_FORM)).isTrue();
        assertThat(isElementVisible(USER_DASHBOARD)).isFalse();
    }

    @Test
    @Order(2)
    @DisplayName("Debería registrar usuario con datos válidos")
    void deberiaRegistrarUsuarioConDatosValidos() {
        // Given
        String nombre = "Juan Pérez";
        String peso = "75.5";

        // When
        registrarUsuario(nombre, peso);

        // Then
        assertThat(isElementVisible(USER_DASHBOARD)).isTrue();
        assertThat(isElementVisible(REGISTRATION_FORM)).isFalse();
        assertThat(getElementText(USUARIO_NOMBRE)).contains(nombre);
        assertThat(getElementText(PESO_ACTUAL)).contains("75.5");
        assertThat(isSuccessMessageVisible()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Debería mostrar error con nombre inválido")
    void deberiaMostrarErrorConNombreInvalido(String nombreInvalido) {
        // Given & When
        driver.findElement(By.cssSelector(NOMBRE_INPUT)).sendKeys(nombreInvalido);
        driver.findElement(By.cssSelector(PESO_INICIAL_INPUT)).sendKeys("75.0");
        driver.findElement(By.cssSelector(BTN_REGISTRAR)).click();

        // Then
        waitForMessage(ERROR_MESSAGE);
        assertThat(isErrorMessageVisible()).isTrue();
        assertThat(isElementVisible(USER_DASHBOARD)).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "-10.5", "abc", ""})
    @DisplayName("Debería mostrar error con peso inválido")
    void deberiaMostrarErrorConPesoInvalido(String pesoInvalido) {
        // Given & When
        driver.findElement(By.cssSelector(NOMBRE_INPUT)).sendKeys("Test User");
        driver.findElement(By.cssSelector(PESO_INICIAL_INPUT)).sendKeys(pesoInvalido);
        driver.findElement(By.cssSelector(BTN_REGISTRAR)).click();

        // Then
        waitForMessage(ERROR_MESSAGE);
        assertThat(isErrorMessageVisible()).isTrue();
        assertThat(isElementVisible(USER_DASHBOARD)).isFalse();
    }

    // =================== TESTS DE ACTUALIZACIÓN DE PESO ===================

    @Test
    @Order(3)
    @DisplayName("Debería actualizar peso correctamente (FIX del bug)")
    void deberiaActualizarPesoCorrectamente() {
        // Given - Registrar usuario primero
        registrarUsuario("María García", "65.0");
        waitForDashboard();

        // When - Actualizar peso
        String nuevoPeso = "70.5";
        actualizarPeso(nuevoPeso);

        // Then
        assertThat(getElementText(PESO_ACTUAL)).contains("70.5");
        assertThat(isSuccessMessageVisible()).isTrue();
        
        // Verificar que NO se restó 1kg (comportamiento del bug)
        assertThat(getElementText(PESO_ACTUAL)).doesNotContain("64.0"); // 65-1=64
    }

    @Test
    @Order(4)
    @DisplayName("Debería detectar el bug original de restar 1kg")
    void deberiaDetectarBugOriginalDeRestar1kg() {
        // Given
        registrarUsuario("Test User Bug", "100.0");
        waitForDashboard();

        // When
        actualizarPeso("95.0");

        // Then - Con la corrección, el peso debería ser 95.0, no 99.0
        String pesoMostrado = getElementText(PESO_ACTUAL);
        assertThat(pesoMostrado).contains("95.0"); // Comportamiento correcto
        assertThat(pesoMostrado).doesNotContain("99.0"); // Comportamiento del bug (100-1=99)
    }

    @ParameterizedTest
    @ValueSource(strings = {"80.5", "75.0", "90.3", "65.7"})
    @DisplayName("Debería aceptar diferentes pesos válidos")
    void deberiaAceptarDiferentesPesosValidos(String peso) {
        // Given
        registrarUsuario("Test User Weights", "70.0");
        waitForDashboard();

        // When
        actualizarPeso(peso);

        // Then
        assertThat(getElementText(PESO_ACTUAL)).contains(peso);
        assertThat(isSuccessMessageVisible()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "0", "-5.5", "abc", ""})
    @DisplayName("Debería rechazar pesos inválidos en actualización")
    void deberiaRechazarPesosInvalidosEnActualizacion(String pesoInvalido) {
        // Given
        registrarUsuario("Test User Invalid", "70.0");
        waitForDashboard();

        // When
        driver.findElement(By.cssSelector(NUEVO_PESO_INPUT)).sendKeys(pesoInvalido);
        driver.findElement(By.cssSelector(BTN_ACTUALIZAR)).click();

        // Then
        waitForMessage(ERROR_MESSAGE);
        assertThat(isErrorMessageVisible()).isTrue();
        assertThat(getElementText(PESO_ACTUAL)).contains("70.0"); // Peso no cambió
    }

    // =================== TESTS DE INTERFAZ DE USUARIO ===================

    @Test
    @Order(5)
    @DisplayName("Debería permitir registro usando tecla Enter")
    void deberiaPermitirRegistroUsandoTeclaEnter() {
        // Given
        driver.findElement(By.cssSelector(NOMBRE_INPUT)).sendKeys("User Enter Test");
        driver.findElement(By.cssSelector(PESO_INICIAL_INPUT)).sendKeys("72.5");

        // When - Presionar Enter en el campo de peso
        driver.findElement(By.cssSelector(PESO_INICIAL_INPUT)).sendKeys(Keys.ENTER);

        // Then
        waitForDashboard();
        assertThat(isElementVisible(USER_DASHBOARD)).isTrue();
        assertThat(getElementText(USUARIO_NOMBRE)).contains("User Enter Test");
    }

    @Test
    @Order(6)
    @DisplayName("Debería permitir actualización usando tecla Enter")
    void deberiaPermitirActualizacionUsandoTeclaEnter() {
        // Given
        registrarUsuario("User Enter Update", "68.0");
        waitForDashboard();

        // When
        driver.findElement(By.cssSelector(NUEVO_PESO_INPUT)).sendKeys("73.0");
        driver.findElement(By.cssSelector(NUEVO_PESO_INPUT)).sendKeys(Keys.ENTER);

        // Then
        waitForMessage(SUCCESS_MESSAGE);
        assertThat(getElementText(PESO_ACTUAL)).contains("73.0");
        assertThat(isSuccessMessageVisible()).isTrue();
    }

    @Test
    @Order(7)
    @DisplayName("Debería limpiar formulario al crear nuevo usuario")
    void deberiaLimpiarFormularioAlCrearNuevoUsuario() {
        // Given
        registrarUsuario("User Reset Test", "77.0");
        waitForDashboard();

        // When
        driver.findElement(By.cssSelector(BTN_NUEVO_USUARIO)).click();

        // Then
        waitForElement(REGISTRATION_FORM);
        assertThat(isElementVisible(REGISTRATION_FORM)).isTrue();
        assertThat(isElementVisible(USER_DASHBOARD)).isFalse();
        assertThat(driver.findElement(By.cssSelector(NOMBRE_INPUT)).getAttribute("value")).isEmpty();
        assertThat(driver.findElement(By.cssSelector(PESO_INICIAL_INPUT)).getAttribute("value")).isEmpty();
    }

    // =================== TESTS DE VALIDACIÓN DE MENSAJES ===================

    @Test
    @Order(8)
    @DisplayName("Debería mostrar mensaje de bienvenida al registrar")
    void deberiaMostrarMensajeBienvenidaAlRegistrar() {
        // Given & When
        registrarUsuario("Welcome User", "69.5");

        // Then
        waitForMessage(SUCCESS_MESSAGE);
        String mensaje = getElementText(SUCCESS_MESSAGE);
        assertThat(mensaje).contains("Bienvenido/a Welcome User");
        assertThat(mensaje).contains("69.5 kg");
        assertThat(mensaje).contains("registrado");
    }

    @Test
    @Order(9)
    @DisplayName("Debería mostrar diferencia de peso al actualizar")
    void deberiaMostrarDiferenciaPesoAlActualizar() {
        // Given
        registrarUsuario("Diff User", "75.0");
        waitForDashboard();

        // When - Aumentar peso
        actualizarPeso("78.5");

        // Then
        waitForMessage(SUCCESS_MESSAGE);
        String mensaje = getElementText(SUCCESS_MESSAGE);
        assertThat(mensaje).contains("78.5 kg");
        assertThat(mensaje).contains("+3.5"); // Diferencia positiva
    }

    // =================== MÉTODOS AUXILIARES ===================

    private void registrarUsuario(String nombre, String peso) {
        driver.findElement(By.cssSelector(NOMBRE_INPUT)).clear();
        driver.findElement(By.cssSelector(NOMBRE_INPUT)).sendKeys(nombre);
        driver.findElement(By.cssSelector(PESO_INICIAL_INPUT)).clear();
        driver.findElement(By.cssSelector(PESO_INICIAL_INPUT)).sendKeys(peso);
        driver.findElement(By.cssSelector(BTN_REGISTRAR)).click();
    }

    private void actualizarPeso(String peso) {
        driver.findElement(By.cssSelector(NUEVO_PESO_INPUT)).clear();
        driver.findElement(By.cssSelector(NUEVO_PESO_INPUT)).sendKeys(peso);
        driver.findElement(By.cssSelector(BTN_ACTUALIZAR)).click();
    }

    private void waitForPageLoad() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
    }

    private void waitForDashboard() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(USER_DASHBOARD)));
    }

    private void waitForElement(String selector) {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
    }

    private void waitForMessage(String messageSelector) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(messageSelector)));
    }

    private boolean isElementVisible(String selector) {
        try {
            WebElement element = driver.findElement(By.cssSelector(selector));
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private String getElementText(String selector) {
        return driver.findElement(By.cssSelector(selector)).getText();
    }

    private boolean isErrorMessageVisible() {
        return isElementVisible(ERROR_MESSAGE) && 
               !getElementText(ERROR_MESSAGE).isEmpty();
    }

    private boolean isSuccessMessageVisible() {
        return isElementVisible(SUCCESS_MESSAGE) && 
               !getElementText(SUCCESS_MESSAGE).isEmpty();
    }
}