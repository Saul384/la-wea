package  com.microservicio.asignaturas.service;
import  com.microservicio.asignaturas.client.ClientFeignCarreras;
import  com.microservicio.asignaturas.dto.*;
import  com.microservicio.asignaturas.entity.Asignatura;
import  com.microservicio.asignaturas.repository.AsignaturaRepository;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
@Service
public class AsignaturaService {
    private final AsignaturaRepository repository;
    private final ClientFeignCarreras clientCarreras;
    public AsignaturaService(
            AsignaturaRepository repository,
            ClientFeignCarreras clientCarreras) {
        this.repository = repository;
        this.clientCarreras = clientCarreras;
    }
    private CarreraDto validarCarrera(Long carreraId) {
        if (carreraId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La carrera es obligatoria"
            );
        }
        try {
            CarreraDto carrera = clientCarreras.obtenerCarrera(carreraId);
            if (carrera == null || carrera.getId() == null
                    || !carreraId.equals(carrera.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "La carrera indicada no existe"
                );
            }
            if (Boolean.FALSE.equals(carrera.getEstado())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "No se puede asignar una carrera inactiva"
                );
            }
            return carrera;
        } catch (FeignException.NotFound e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La carrera #" + carreraId + " no existe"
            );
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible consultar el microservicio de Carreras"
            );
        }
    }
    private AsignaturaDto convertirDTO(Asignatura entidad) {
        AsignaturaDto dto = new AsignaturaDto();
        dto.setId(entidad.getId());
        dto.setNombre(entidad.getNombre());
        dto.setDescripcion(entidad.getDescripcion());
        dto.setEstado(entidad.getEstado());
        dto.setCarreraId(entidad.getCarreraId());
        try {
            CarreraDto carrera =
                    clientCarreras.obtenerCarrera(entidad.getCarreraId());
            if (carrera != null) {
                dto.setNombreCarrera(carrera.getNombre());
            }
        } catch (Exception e) {
            dto.setNombreCarrera(
                                  "Carrera #" + entidad.getCarreraId()
            );
        }
        return dto;
    }
    @Transactional(readOnly = true)
    public List<AsignaturaDto> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirDTO)
                .toList();
    }
    @Transactional(readOnly = true)
    public AsignaturaDto buscar(Long id) {
        Asignatura entidad = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Asignatura no encontrada"
                ));
        return convertirDTO(entidad);
    }
    @Transactional
    public AsignaturaDto crear(AsignaturaDto datos) {
        validarDatos(datos);
        validarCarrera(datos.getCarreraId());
        Asignatura nueva = new Asignatura();
        nueva.setNombre(datos.getNombre().trim());
        nueva.setDescripcion(datos.getDescripcion());
        nueva.setEstado(true);
        nueva.setCarreraId(datos.getCarreraId());
        return convertirDTO(repository.save(nueva));
    }
    @Transactional
    public AsignaturaDto actualizar(Long id, AsignaturaDto datos) {
        Asignatura entidad = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Asignatura no encontrada"
                ));
        if (datos.getNombre() != null) {
            if (datos.getNombre().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "El nombre no puede estar vacío"
                );
            }
            entidad.setNombre(datos.getNombre().trim());
        }
        if (datos.getDescripcion() != null) {
            entidad.setDescripcion(datos.getDescripcion());
        }
        if (datos.getCarreraId() != null) {
            validarCarrera(datos.getCarreraId());
            entidad.setCarreraId(datos.getCarreraId());
        }
        // El estado se modifica mediante activar/desactivar.
        return convertirDTO(repository.save(entidad));
    }
    @Transactional
    public AsignaturaDto cambiarEstado(Long id, boolean estado) {
        Asignatura entidad = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Asignatura no encontrada"
                ));
        entidad.setEstado(estado);
        return convertirDTO(repository.save(entidad));
    }
    @Transactional(readOnly = true)
    public List<EntityAsignaturaDto> obtenerPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return repository.findAllById(ids).stream()
                .map(asignatura -> {
                                        EntityAsignaturaDto dto = new EntityAsignaturaDto();
                    dto.setId(asignatura.getId());
                    dto.setNombreAsignatura(asignatura.getNombre());
                    return dto;
                })
                .toList();
    }
    private void validarDatos(AsignaturaDto datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre es obligatorio"
            );
        }
        if (datos.getNombre().trim().length() > 120) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no puede superar 120 caracteres"
            );
        }
    }
}
