package com.healthtrack.healthtrack_platform.performance;

import com.healthtrack.healthtrack_platform.model.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Pruebas de rendimiento para la clase Usuario
 * Evalúa el tiempo de respuesta y consumo de memoria
 */
@DisplayName("Usuario Performance Tests")
class UsuarioPerformanceTest {

    private static final int NUMERO_USUARIOS = 10000;
    private static final int NUMERO_ACTUALIZACIONES = 1000;
    private static final long TIEMPO_MAXIMO_MS = 1000; // 1 segundo

    @Test
    @DisplayName("Creación masiva de usuarios debería ser eficiente")
    void creacionMasivaUsuariosDeberiaSerEficiente() {
        // Given
        List<Usuario> usuarios = new ArrayList<>();
        Instant inicio = Instant.now();

        // When
        for (int i = 0; i < NUMERO_USUARIOS; i++) {
            usuarios.add(new Usuario("Usuario" + i, 70.0 + i));
        }
        
        Instant fin = Instant.now();
        Duration duracion = Duration.between(inicio, fin);

        // Then
        assertThat(duracion.toMillis())
                .describedAs("La creación de %d usuarios debería tomar menos de %d ms", 
                           NUMERO_USUARIOS, TIEMPO_MAXIMO_MS)
                .isLessThan(TIEMPO_MAXIMO_MS);
        
        assertThat(usuarios).hasSize(NUMERO_USUARIOS);
    }

    @Test
    @DisplayName("Actualizaciones masivas de peso deberían ser eficientes")
    void actualizacionesMasivasPesoDeberianSerEficientes() {
        // Given
        Instant inicio = Instant.now();

        // When
        for (int i = 0; i < NUMERO_ACTUALIZACIONES; i++) {
            // Creamos un nuevo usuario para cada iteración para evitar la restricción de 48h
            Usuario usuarioTemp = new Usuario("UsuarioTemp" + i, 75.0);
            usuarioTemp.actualizarPeso(75.0 + i);
        }
        
        Instant fin = Instant.now();
        Duration duracion = Duration.between(inicio, fin);

        // Then
        assertThat(duracion.toMillis())
                .describedAs("Las actualizaciones de peso deberían ser eficientes")
                .isLessThan(TIEMPO_MAXIMO_MS);
    }

    @RepeatedTest(5)
    @DisplayName("Operaciones básicas deberían ser consistentemente rápidas")
    void operacionesBasicasDeberianSerConsistentementeRapidas() {
        // Given
        Instant inicio = Instant.now();
        
        // When
        Usuario usuario = new Usuario("UsuarioPerformance", 80.0);
        usuario.actualizarPeso(85.0);
        String info = usuario.toString();
        boolean puedeActualizar = usuario.puedeActualizarPeso();
        
        Instant fin = Instant.now();
        Duration duracion = Duration.between(inicio, fin);

        // Then
        assertThat(duracion.toNanos())
                .describedAs("Las operaciones básicas deberían ser muy rápidas")
                .isLessThan(1_000_000); // 1 millisegundo en nanosegundos
        
        // Validamos que las operaciones produjeron resultados válidos
        assertThat(usuario.getPeso()).isEqualTo(85.0);
        assertThat(info).isNotEmpty();
        assertThat(puedeActualizar).isFalse(); // No puede actualizar porque ya actualizó recientemente
    }

    @Test
    @DisplayName("Consumo de memoria debería ser predecible")
    void consumoMemoriaDeberiaSerPredecible() {
        // Given
        Runtime runtime = Runtime.getRuntime();
        runtime.gc(); // Forzar garbage collection
        long memoriaInicial = runtime.totalMemory() - runtime.freeMemory();
        
        List<Usuario> usuarios = new ArrayList<>();

        // When
        for (int i = 0; i < 1000; i++) {
            usuarios.add(new Usuario("Usuario" + i, 70.0));
        }

        runtime.gc(); // Forzar garbage collection
        long memoriaFinal = runtime.totalMemory() - runtime.freeMemory();
        long memoriaUsada = memoriaFinal - memoriaInicial;

        // Then
        assertThat(memoriaUsada)
                .describedAs("El consumo de memoria debería ser razonable")
                .isLessThan(10_000_000); // 10 MB
        
        assertThat(usuarios).hasSize(1000);
    }

    @Test
    @DisplayName("Método equals debería ser eficiente")
    void metodoEqualsDeberiaSerEficiente() {
        // Given
        Usuario usuario1 = new Usuario("UsuarioTest", 75.0);
        Usuario usuario2 = new Usuario("UsuarioTest", 80.0);
        Usuario usuario3 = new Usuario("OtroUsuario", 75.0);
        
        Instant inicio = Instant.now();

        // When
        boolean resultado1 = usuario1.equals(usuario2); // true
        boolean resultado2 = usuario1.equals(usuario3); // false
        int hash1 = usuario1.hashCode();
        int hash2 = usuario2.hashCode();
        
        Instant fin = Instant.now();
        Duration duracion = Duration.between(inicio, fin);

        // Then
        assertThat(duracion.toNanos())
                .describedAs("Las operaciones equals y hashCode deberían ser muy rápidas")
                .isLessThan(100_000); // 0.1 millisegundos
        
        assertThat(resultado1).isTrue();
        assertThat(resultado2).isFalse();
        assertThat(hash1).isEqualTo(hash2);
    }

    @Test
    @DisplayName("toString debería ser eficiente")
    void toStringDeberiaSerEficiente() {
        // Given
        Usuario usuario = new Usuario("UsuarioTestRendimiento", 75.5);
        Instant inicio = Instant.now();

        // When
        String resultado = null;
        for (int i = 0; i < 10000; i++) {
            resultado = usuario.toString();
        }
        
        Instant fin = Instant.now();
        Duration duracion = Duration.between(inicio, fin);

        // Then
        assertThat(duracion.toMillis())
                .describedAs("10000 llamadas a toString deberían ser rápidas")
                .isLessThan(100);
        
        assertThat(resultado)
                .contains("UsuarioTestRendimiento")
                .contains("75.5");
    }
}