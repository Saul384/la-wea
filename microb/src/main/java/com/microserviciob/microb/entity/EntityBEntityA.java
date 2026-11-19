package com.microserviciob.microb.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "programa_division")
public class EntityBEntityA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "carrera_division_id")
    private Long entityAId;
}
