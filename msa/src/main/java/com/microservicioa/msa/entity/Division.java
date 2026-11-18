package com.microservicioa.msa.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

@Entity 
@Table(name = "divisiones")
@Data 
public class Division {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado;
    
    @Column(nullable = false)
    private Boolean publica = true; // Visibilidad en la página web pública (HU08)

    @Column(nullable = false, length = 200)
    private String descripcion;

    @OneToMany(mappedBy = "division")
    @JsonIgnore
    @ToString.Exclude
    private List<Carrera> carreras;
}