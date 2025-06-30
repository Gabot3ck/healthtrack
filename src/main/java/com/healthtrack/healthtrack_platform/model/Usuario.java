package com.healthtrack.healthtrack_platform.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de Usuario para la plataforma HealthTrack
 * Permite el monitoreo del peso con restricción de actualización cada 48 horas
 */
public class Usuario {
    private String nombre;
    private double peso;
    private LocalDateTime ultimaActualizacion;
    private static final int HORAS_MINIMAS_ACTUALIZACION = 48;

    /**
     * Constructor para crear un nuevo usuario
     * @param nombre Nombre del usuario
     * @param peso Peso inicial del usuario en kg
     */
    public Usuario(String nombre, double peso) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del usuario no puede ser nulo o vacío");
        }
        if (peso < 0) {
            throw new IllegalArgumentException("El peso no puede ser negativo");
        }
        
        this.nombre = nombre.trim();
        this.peso = peso;
        
        this.ultimaActualizacion = null;
    }

    // Getters
    public String getNombre() {
        return nombre;
    }

    public double getPeso() {
        return peso;
    }

    public LocalDateTime getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    /**
     * Actualiza el peso del usuario
     * CORRECCIÓN: Ahora asigna correctamente el nuevo peso en lugar de restar 1kg
     * @param nuevoPeso Nuevo peso del usuario en kg
     */
    public void actualizarPeso(double nuevoPeso) {
        if (nuevoPeso < 0) {
            throw new IllegalArgumentException("El peso no puede ser negativo");
        }
        
        if (!puedeActualizarPeso()) {
            throw new IllegalStateException("No se puede actualizar el peso. Deben pasar al menos 48 horas desde la última actualización");
        }
        
        // CORRECCIÓN DEL BUG: Asignar el nuevo peso correctamente
        this.peso = nuevoPeso;
        this.ultimaActualizacion = LocalDateTime.now();
    }

    /**
     * Verificar si el usuario puede actualizar su peso (han pasado 48 horas)
     * @return true si puede actualizar, false en caso contrario
     */
    public boolean puedeActualizarPeso() {
        // Si nunca ha actualizado el peso, puede hacerlo
        if (ultimaActualizacion == null) {
            return true;
        }
        
        LocalDateTime proximaActualizacionPermitida = ultimaActualizacion.plusHours(HORAS_MINIMAS_ACTUALIZACION);
        return LocalDateTime.now().isAfter(proximaActualizacionPermitida);
    }

    /**
     * Muestra la información del usuario
     */
    public void mostrarInformacion() {
        System.out.printf("Usuario: %s, Peso Actual: %.2f kg, Última Actualización: %s%n", 
                         nombre, peso, ultimaActualizacion);
    }

    @Override
    public String toString() {
        return String.format("Usuario{nombre='%s', peso=%.2f kg, ultimaActualizacion=%s}", 
                           nombre, peso, ultimaActualizacion);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Usuario usuario = (Usuario) obj;
        return Objects.equals(nombre, usuario.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}