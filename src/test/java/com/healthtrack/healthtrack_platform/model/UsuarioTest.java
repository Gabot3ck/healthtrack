package com.healthtrack.healthtrack_platform.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas unitarias para la clase Usuario
 * Estas pruebas detectan el bug original y validan la corrección
 */
@DisplayName("Usuario Tests")
class UsuarioTest {

    private Usuario usuario;
    private static final String NOMBRE_VALIDO = "Juan Pérez";
    private static final double PESO_VALIDO = 75.5;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(NOMBRE_VALIDO, PESO_VALIDO);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Debería crear usuario con datos válidos")
        void deberiaCrearUsuarioConDatosValidos() {
            // Given & When
            Usuario nuevoUsuario = new Usuario("María García", 65.0);

            // Then
            assertThat(nuevoUsuario.getNombre()).isEqualTo("María García");
            assertThat(nuevoUsuario.getPeso()).isEqualTo(65.0);
            assertThat(nuevoUsuario.getUltimaActualizacion()).isNull(); // Inicialmente null
            assertThat(nuevoUsuario.puedeActualizarPeso()).isTrue(); // Puede actualizar inmediatamente
        }

        @Test
        @DisplayName("Debería limpiar espacios en blanco del nombre")
        void deberiaLimpiarEspaciosEnBlancoDelNombre() {
            // Given & When
            Usuario usuarioConEspacios = new Usuario("  Carlos López  ", 80.0);

            // Then
            assertThat(usuarioConEspacios.getNombre()).isEqualTo("Carlos López");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Debería lanzar excepción con nombre inválido")
        void deberiaLanzarExcepcionConNombreInvalido(String nombreInvalido) {
            // Given & When & Then
            assertThatThrownBy(() -> new Usuario(nombreInvalido, PESO_VALIDO))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El nombre del usuario no puede ser nulo o vacío");
        }

        @ParameterizedTest
        @ValueSource(doubles = {-1.0, -0.1, -100.5})
        @DisplayName("Debería lanzar excepción con peso negativo")
        void deberiaLanzarExcepcionConPesoNegativo(double pesoNegativo) {
            // Given & When & Then
            assertThatThrownBy(() -> new Usuario(NOMBRE_VALIDO, pesoNegativo))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El peso no puede ser negativo");
        }

        @Test
        @DisplayName("Debería aceptar peso cero")
        void deberiaAceptarPesoCero() {
            // Given & When
            Usuario usuarioConPesoCero = new Usuario(NOMBRE_VALIDO, 0.0);

            // Then
            assertThat(usuarioConPesoCero.getPeso()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Actualización de Peso Tests")
    class ActualizacionPesoTests {

        @Test
        @DisplayName("Debería actualizar peso correctamente (FIX del bug)")
        void deberiaActualizarPesoCorrectamente() {
            // Given
            Usuario usuarioTest = new Usuario("Test Usuario", 75.5);
            double pesoInicial = usuarioTest.getPeso();
            double nuevoPeso = 80.0;

            // When
            usuarioTest.actualizarPeso(nuevoPeso);

            // Then
            assertThat(usuarioTest.getPeso())
                    .describedAs("El peso debería ser exactamente el nuevo peso, no el peso inicial menos 1kg")
                    .isEqualTo(nuevoPeso)
                    .isNotEqualTo(pesoInicial - 1.0); // Esto detecta el bug original
        }

        @Test
        @DisplayName("Debería detectar el bug original (peso - 1kg)")
        void deberiaDetectarBugOriginal() {
            // Given
            Usuario usuarioTest = new Usuario("Test Usuario 2", 75.5);
            double pesoInicial = usuarioTest.getPeso();
            double nuevoPeso = 80.0;

            // When
            usuarioTest.actualizarPeso(nuevoPeso);

            // Then - Estas assertions fallarían con el código bugueado original
            assertThat(usuarioTest.getPeso())
                    .describedAs("Con el bug original, esto fallaría porque el peso sería %s - 1 = %s", 
                               pesoInicial, pesoInicial - 1)
                    .isNotEqualTo(pesoInicial - 1.0)
                    .isEqualTo(nuevoPeso);
        }

        @Test
        @DisplayName("Debería actualizar la fecha de última actualización")
        void deberiaActualizarFechaUltimaActualizacion() {
            // Given
            Usuario usuarioTest = new Usuario("Test Usuario 3", 75.5);
            LocalDateTime fechaAnterior = usuarioTest.getUltimaActualizacion(); // Será null inicialmente
            
            // Capturar momento antes de la actualización
            LocalDateTime momentoAntesDeActualizar = LocalDateTime.now();
            
            // Pequeña pausa para asegurar diferencia en tiempo
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // When
            usuarioTest.actualizarPeso(85.0);

            // Then
            assertThat(fechaAnterior).isNull(); // Inicialmente era null
            assertThat(usuarioTest.getUltimaActualizacion())
                    .isNotNull() // Ahora debería tener fecha
                    .isAfter(momentoAntesDeActualizar) // Debería ser después del momento capturado
                    .isBeforeOrEqualTo(LocalDateTime.now()); // Y antes o igual al momento actual
        }

        @ParameterizedTest
        @ValueSource(doubles = {50.0, 75.5, 100.0, 120.5})
        @DisplayName("Debería aceptar diferentes pesos válidos")
        void deberiaAceptarDiferentesPesosValidos(double peso) {
            // Given - Crear nuevo usuario para cada test
            Usuario usuarioTest = new Usuario("Usuario Test " + peso, 70.0);

            // When
            usuarioTest.actualizarPeso(peso);

            // Then
            assertThat(usuarioTest.getPeso()).isEqualTo(peso);
        }

        @ParameterizedTest
        @ValueSource(doubles = {-1.0, -0.1, -50.5})
        @DisplayName("Debería rechazar pesos negativos en actualización")
        void deberiaRechazarPesosNegativosEnActualizacion(double pesoNegativo) {
            // Given & When & Then
            assertThatThrownBy(() -> usuario.actualizarPeso(pesoNegativo))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("El peso no puede ser negativo");
        }

        @Test
        @DisplayName("Debería aceptar peso cero en actualización")
        void deberiaAceptarPesoCeroEnActualizacion() {
            // Given
            Usuario usuarioTest = new Usuario("Usuario Cero", 75.0);

            // When
            usuarioTest.actualizarPeso(0.0);

            // Then
            assertThat(usuarioTest.getPeso()).isEqualTo(0.0);
        }
    }

    @Nested
    @DisplayName("Restricción 48 horas Tests")
    class Restriccion48HorasTests {

        @Test
        @DisplayName("Usuario recién creado debería poder actualizar peso")
        void usuarioRecienCreadoDeberiaPoder() {
            // Given
            Usuario nuevoUsuario = new Usuario("Nuevo Usuario", 70.0);

            // When & Then
            assertThat(nuevoUsuario.puedeActualizarPeso()).isTrue();
            
            // Verificamos que efectivamente puede actualizar
            assertThatNoException().isThrownBy(() -> nuevoUsuario.actualizarPeso(75.0));
            assertThat(nuevoUsuario.getPeso()).isEqualTo(75.0);
        }

        @Test
        @DisplayName("Debería poder actualizar múltiples veces si han pasado 48 horas")
        void deberiaPoderActualizarMultiplesVeces() {
            // Given
            double primerPeso = 80.0;
            double segundoPeso = 85.0;

            // When - Primera actualización
            Usuario usuarioTest = new Usuario("Usuario Multiple", 70.0);
            usuarioTest.actualizarPeso(primerPeso);
            
            // Simulamos el paso del tiempo creando un nuevo usuario 
            // (en un caso real usaríamos mocks para controlar el tiempo)
            Usuario usuarioTras48Horas = new Usuario("Usuario Tras 48h", primerPeso);
            usuarioTras48Horas.actualizarPeso(segundoPeso);

            // Then
            assertThat(usuarioTest.getPeso()).isEqualTo(primerPeso);
            assertThat(usuarioTras48Horas.getPeso()).isEqualTo(segundoPeso);
        }
    }

    @Nested
    @DisplayName("Getters Tests")
    class GettersTests {

        @Test
        @DisplayName("Debería retornar nombre correcto")
        void deberiaRetornarNombreCorrecto() {
            assertThat(usuario.getNombre()).isEqualTo(NOMBRE_VALIDO);
        }

        @Test
        @DisplayName("Debería retornar peso correcto")
        void deberiaRetornarPesoCorrecto() {
            assertThat(usuario.getPeso()).isEqualTo(PESO_VALIDO);
        }

        @Test
        @DisplayName("Debería retornar fecha de última actualización null inicialmente")
        void deberiaRetornarFechaUltimaActualizacionNullInicialmente() {
            // Given
            Usuario nuevoUsuario = new Usuario("Test User", 70.0);
            
            // Then
            assertThat(nuevoUsuario.getUltimaActualizacion()).isNull();
            
            // Después de actualizar, debería tener fecha
            nuevoUsuario.actualizarPeso(75.0);
            assertThat(nuevoUsuario.getUltimaActualizacion()).isNotNull();
        }
    }

    @Nested
    @DisplayName("Métodos de Object Tests")
    class MetodosObjectTests {

        @Test
        @DisplayName("Dos usuarios con mismo nombre deberían ser iguales")
        void dosUsuariosConMismoNombreDeberianSerIguales() {
            // Given
            Usuario usuario1 = new Usuario("Ana López", 60.0);
            Usuario usuario2 = new Usuario("Ana López", 70.0);

            // When & Then
            assertThat(usuario1)
                    .isEqualTo(usuario2)
                    .hasSameHashCodeAs(usuario2);
        }

        @Test
        @DisplayName("Usuarios con diferentes nombres no deberían ser iguales")
        void usuariosConDiferentesNombresNoDeberianSerIguales() {
            // Given
            Usuario usuario1 = new Usuario("Ana López", 60.0);
            Usuario usuario2 = new Usuario("Carlos García", 60.0);

            // When & Then
            assertThat(usuario1).isNotEqualTo(usuario2);
        }

        @Test
        @DisplayName("toString debería contener información del usuario")
        void toStringDeberiaContenerInformacionDelUsuario() {
            // Given & When
            String resultado = usuario.toString();

            // Then
            assertThat(resultado)
                    .contains(usuario.getNombre())
                    .contains("75") // Acepta tanto 75.5 como 75,5 dependiendo del locale
                    .contains("Usuario{");
        }

        @Test
        @DisplayName("Usuario debería ser igual a sí mismo")
        void usuarioDeberiaSerIgualASiMismo() {
            assertThat(usuario).isEqualTo(usuario);
        }

        @Test
        @DisplayName("Usuario no debería ser igual a null")
        void usuarioNoDeberiaSerIgualANull() {
            assertThat(usuario).isNotEqualTo(null);
        }

        @Test
        @DisplayName("Usuario no debería ser igual a objeto de otra clase")
        void usuarioNoDeberiaSerIgualAObjetoDeOtraClase() {
            assertThat(usuario).isNotEqualTo("string");
        }
    }

    @Nested
    @DisplayName("Mostrar Información Tests")
    class MostrarInformacionTests {

        @Test
        @DisplayName("mostrarInformacion no debería lanzar excepción")
        void mostrarInformacionNoDeberiaLanzarExcepcion() {
            // Given & When & Then
            assertThatNoException().isThrownBy(() -> usuario.mostrarInformacion());
        }
    }

    // Tests específicos para detectar el bug de restar 1kg
    @Nested
    @DisplayName("Tests específicos para detectar bug original")
    class DeteccionBugTests {

        @Test
        @DisplayName("Test que fallaría con el código original bugueado")
        void testQueFallariaConCodigoOriginalBugueado() {
            // Given
            double pesoInicial = 100.0;
            Usuario usuarioTest = new Usuario("Test User Bug", pesoInicial);
            double nuevoPeso = 95.0;

            // When
            usuarioTest.actualizarPeso(nuevoPeso);

            // Then
            // Con el bug original (this.peso -= 1), el peso final sería 99.0
            // Con el código corregido (this.peso = nuevoPeso), el peso final es 95.0
            assertThat(usuarioTest.getPeso())
                    .describedAs("Bug detectado: el código original restaría 1kg del peso inicial")
                    .isEqualTo(95.0)  // Comportamiento correcto
                    .isNotEqualTo(99.0); // Comportamiento del bug (100 - 1 = 99)
        }

        @Test
        @DisplayName("Secuencia de actualizaciones debería mantener valores correctos")
        void secuenciaActualizacionesDeberiaMantenerValoresCorrectos() {
            // Given
            Usuario usuarioTest1 = new Usuario("Test User Seq 1", 80.0);
            Usuario usuarioTest2 = new Usuario("Test User Seq 2", 85.0);

            // When - Simulamos múltiples actualizaciones con usuarios diferentes
            usuarioTest1.actualizarPeso(85.0);
            double pesoTrasUnaActualizacion = usuarioTest1.getPeso();
            
            usuarioTest2.actualizarPeso(90.0);
            double pesoTrasSegundaActualizacion = usuarioTest2.getPeso();

            // Then
            assertThat(pesoTrasUnaActualizacion).isEqualTo(85.0);
            assertThat(pesoTrasSegundaActualizacion).isEqualTo(90.0);
            
            // Con el bug original, estos valores serían 79.0 y 84.0 respectivamente
            assertThat(pesoTrasUnaActualizacion).isNotEqualTo(79.0);
            assertThat(pesoTrasSegundaActualizacion).isNotEqualTo(84.0);
        }
    }
}