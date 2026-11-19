package com.microserviciob.microb.dto;

import lombok.Data;

@Data
public class EntityADto {
    private Long id;
    private String nombre;
    private Boolean estado;
    private Boolean publica;
    private String descripcion;

    // Métodos de compatibilidad
    public String getNombreA() {
        return this.nombre;
    }

    public void setNombreA(String nombreA) {
        this.nombre = nombreA;
    }
}
