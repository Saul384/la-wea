package  com.microservicio.asignaturas.entity;
import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "asignaturas")
@Data
public class Asignatura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
    @Column(nullable = false, length = 120)
    private String nombre;
    @Column(length = 500)
    private String descripcion;
    @Column(nullable = false)
    private Boolean estado = true;
    // ID de Carrera en el microservicio externo.
    // No se usa @ManyToOne porque Carrera pertenece a otro microservicio.
    @Column(name = "carrera_id", nullable = false)
    private Long carreraId;
}