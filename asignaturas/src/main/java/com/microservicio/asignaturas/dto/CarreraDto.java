package  com.microservicio.asignaturas.dto;
import lombok.Data;
@Data
public class CarreraDto {
    private Long id;
    private String nombre;
    private Boolean estado;
    private String descripcion;
}