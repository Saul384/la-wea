package com.microservicio.alumnos.dto;

import lombok.Data;

@Data
public class EntityAlumnoDtoList {
    private Long id;
    private String nombreAlumno;
    private String matricula;
    private Boolean estadoAlumno;
    private Long entityAsignaturaId; // ID de la división
    private String nombreAsignatura;

    // Métodos de compatibilidad con frontend anterior
    public String getNombreAlumno() {
        return this.nombreAlumno;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public String getNombreAsignatura() {
        return this.nombreAsignatura;
    }

    public void setNombreAsignatura(String nombreAsignatura) {
        this.nombreAsignatura = nombreAsignatura;
    }
}
