package main.java.com.microservicio.alumnos.entity;

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

import com.microservicio.alumnos.entity.EntityAlumnoAsignatura;

@Entity
@Table(name = "alumnos")
@Data
public class EntityAlumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombreAlumno;

    @Column(nullable = false, length = 50)
    private String matricula;

    @Column(nullable = false)
    private Boolean estadoAlumno = true;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "asignatura_id")
    private List<EntityAlumnoEntityAsignatura> listaConEntityAsignatura = new ArrayList<>();

    // Métodos de compatibilidad con código anterior y frontend
    public String getNombreAlumno() {
        return this.nombreAlumno;
    }

    public String getMatricula() {
        return this.matricula;
    }

    public void setNombreAlumno(String nombreAlumno) {
        this.nombreAlumno = nombreAlumno;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
