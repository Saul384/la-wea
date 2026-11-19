package com.microservicioa.msa.dto;

import lombok.Data;

@Data
public class dto {
    private Long id;
    private String nombre;
    private Boolean estado;
    private Boolean publica;
    private String descripcion;

    // Compatibilidad con código y frontend anteriores
    public String getNombreA() {
        return this.nombre;
    }

    public void setNombreA(String nombreA) {
        this.nombre = nombreA;
    }
}
