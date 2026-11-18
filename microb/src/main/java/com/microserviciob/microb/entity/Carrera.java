package com.microserviciob.microb.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "carreras")
@Data
public class Carrera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado;

    @Column(nullable = false)
    private Boolean publica = true; // Visibilidad en la página web pública (HU08)

    @Column(nullable = false)
    private Boolean intensiva;

    @Column(nullable = false, length = 200)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "division_id", nullable = false)
    private Division division;
}