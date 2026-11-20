package com.microservicio.alumnos.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.microservicio.alumnos.dto.EntityAsignaturaDto;
import java.util.List;

@FeignClient(name = "asignaturas", url = "${asignaturas.url:}") 
public interface ClientFeignAsignatura {
    @PostMapping("/api/asignaturas/by-ids")
    List<EntityAsignaturaDto> obtenerDTOsDelAsignatura(@RequestBody List<Long> ids);
}
