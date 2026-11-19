package com.microservicioa.msa.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table(name = "divisiones")
@Data 
public class entidad {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado = true;
    
    @Column(nullable = false)
    private Boolean publica = true; // Visibilidad en la página web pública (HU08)

    @Column(length = 200)
    private String descripcion;

    // Compatibilidad con código y frontend anteriores
    public String getNombreA() {
        return this.nombre;
    }

    public void setNombreA(String nombreA) {
        this.nombre = nombreA;
    }
}
