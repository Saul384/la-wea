package com.microserviciob.microb.dto;

import lombok.Data;

@Data
public class dto {
    private Long id;
    private String nombreB;
    /** ID de la entidad A (MS-A) relacionada; null si no tiene. */
    private Integer entityAId;
}