package com.microservicio.alumnos.dto;

import lombok.Data;

@Data
public class dto {
    private Long id;
    private String nombreAlumno;
    private String matricula;
    private Boolean estadoAlumno;
    private Long entityAsignaturaId; // ID de la asignatura asociada

    // Métodos de compatibilidad
    public String getNombreAlumno() {
        return this.nombreAlumno;
    }

    public String getMatricula() {
        return this.matricula;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
