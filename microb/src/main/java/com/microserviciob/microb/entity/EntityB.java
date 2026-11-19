package com.microserviciob.microb.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "programas_educativos")
@Data
public class EntityB {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado = true;

    @Column(nullable = false)
    private Boolean publica = true; // Visibilidad en la página web pública (HU08)

    @Column(nullable = false)
    private Boolean intensiva = false;

    @Column(length = 200)
    private String descripcion;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "programa_id")
    private List<EntityBEntityA> listaConEntityA = new ArrayList<>();

    // Métodos de compatibilidad con código anterior y frontend
    public String getNombreB() {
        return this.nombre;
    }

    public void setNombreB(String nombreB) {
        this.nombre = nombreB;
    }
}
