package  com.microservicio.asignaturas.controller;
import  com.microservicio.asignaturas.dto.AsignaturaDto;
import  com.microservicio.asignaturas.dto.EntityAsignaturaDto;
import  com.microservicio.asignaturas.service.AsignaturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/asignaturas")
public class AsignaturaController {
    private final AsignaturaService service;
    public AsignaturaController(AsignaturaService service) {
        this.service = service;
    }
    // Consulta general
    @GetMapping
    public List<AsignaturaDto> listar() {
        return service.listar();
    }
    // Consulta por ID
    @GetMapping("/{id:\\d+}")
    public AsignaturaDto buscar(@PathVariable Long id) {
        return service.buscar(id);
    }
    // Alta
    @PostMapping
    public ResponseEntity<AsignaturaDto> crear(
            @RequestBody AsignaturaDto datos) {
        return ResponseEntity.status(201).body(service.crear(datos));
    }
    // Edición
    @PutMapping("/{id}")
    public AsignaturaDto actualizar(
            @PathVariable Long id,
            @RequestBody AsignaturaDto datos) {
        return service.actualizar(id, datos);
    }
    // Baja temporal
    @PatchMapping("/{id}/desactivar")
    public AsignaturaDto desactivar(@PathVariable Long id) {
        return service.cambiarEstado(id, false);
    }
    // Reactivar
    @PatchMapping("/{id}/activar")
    public AsignaturaDto activar(@PathVariable Long id) {
        return service.cambiarEstado(id, true);
           }
    // Endpoint requerido por el microservicio de Alumnos
    @PostMapping("/by-ids")
    public List<EntityAsignaturaDto> obtenerPorIds(
            @RequestBody List<Long> ids) {
        return service.obtenerPorIds(ids);
    }
}