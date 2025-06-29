package com.healthtrack.healthtrack_platform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for simple App using JUnit 5
 */
public class AppTest {

    @Test
    @DisplayName("Test básico de la aplicación")
    public void testApp() {
        assertTrue(true, "Test básico debe pasar");
    }
    
    @Test
    @DisplayName("Test de ejemplo con assertion")
    public void testExample() {
        String expected = "HealthTrack";
        String actual = "HealthTrack";
        assertEquals(expected, actual, "Los strings deben ser iguales");
    }
}