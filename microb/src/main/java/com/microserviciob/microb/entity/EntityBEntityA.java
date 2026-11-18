package com.microserviciob.microb.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "entity_b_entity_a")
public class EntityBEntityA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_a_id")
    private Integer entityAId;
}