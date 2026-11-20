package com.microservicio.alumnos.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "alumno_asignatura")
public class EntityAlumnoAsignatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alumno_asignatura_id")
    private Long entityAsignaturaId;
}
