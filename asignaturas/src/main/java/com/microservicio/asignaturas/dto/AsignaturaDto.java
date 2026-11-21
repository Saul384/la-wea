package com.microservicio.asignaturas.dto;
import lombok.Data;
@Data
public class AsignaturaDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Boolean estado;
    private Long carreraId;
    private String nombreCarrera;
}