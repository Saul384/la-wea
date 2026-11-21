package  com.microservicio.asignaturas.client;
import  com.microservicio.asignaturas.dto.CarreraDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
@FeignClient(name = "microb", url = "${carreras.url:}")
public interface ClientFeignCarreras {
    @GetMapping("/api/b/{id}")
    CarreraDto obtenerCarrera(@PathVariable("id") Long id);
}
