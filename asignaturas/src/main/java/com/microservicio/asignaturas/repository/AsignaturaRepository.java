package  com.microservicio.asignaturas.repository;
import   com.microservicio.asignaturas.entity.Asignatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface AsignaturaRepository
        extends JpaRepository<Asignatura, Long> {
}