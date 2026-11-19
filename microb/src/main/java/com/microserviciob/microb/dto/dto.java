package com.microserviciob.microb.dto;

import lombok.Data;

@Data
public class dto {
    private Long id;
    private String nombre;
    private Boolean estado;
    private Boolean publica;
    private Boolean intensiva;
    private String descripcion;
    private Long entityAId; // ID de la división asociada

    // Métodos de compatibilidad
    public String getNombreB() {
        return this.nombre;
    }

    public void setNombreB(String nombreB) {
        this.nombre = nombreB;
    }
}
