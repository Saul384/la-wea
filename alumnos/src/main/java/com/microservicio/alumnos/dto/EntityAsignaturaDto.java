package com.microservicio.alumnos.dto;

import lombok.Data;

@Data
public class EntityAsignaturaDto {
    private Long id;
    private String nombreAsignatura;
    private Boolean estadoAsignatura;

    // Métodos de compatibilidad
    public String getNombreAsignatura() {
        return this.nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }
}
