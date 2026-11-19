package com.microserviciob.microb.dto;

import lombok.Data;

@Data
public class EntityBDtoList {
    private Long id;
    private String nombre;
    private Boolean estado;
    private Boolean publica;
    private Boolean intensiva;
    private String descripcion;
    private Long entityAId; // ID de la división
    private String nombreDivision;

    // Métodos de compatibilidad con frontend anterior
    public String getNombreB() {
        return this.nombre;
    }

    public void setNombreB(String nombreB) {
        this.nombre = nombreB;
    }

    public String getNombreA() {
        return this.nombreDivision;
    }

    public void setNombreA(String nombreA) {
        this.nombreDivision = nombreA;
    }
}
