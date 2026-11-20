package com.microservicio.alumnos.service;

import com.microservicio.alumnos.client.ClientFeignAsignatura;
import com.microservicio.alumnos.dto.EntityAsignaturaDto;
import com.microservicio.alumnos.dto.EntityAlumnoDtoList;
import com.microservicio.alumnos.dto.dto;
import com.microservicio.alumnos.entity.EntityAlumno;
import com.microservicio.alumnos.entity.EntityAlumnoAsignatura;
import com.microservicio.alumnos.repository.repositoryAlumno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class servicio {

    @Autowired
    private repositoryAlumno repositoryAlumno;

    @Autowired
    private ClientFeignAsignatura clientFeignAsignatura;

    public dto convertirDTO(EntityAlumno entityAlumno) {
        dto datos = new dto();
        datos.setId(entityAlumno.getId());
        datos.setNombre(entityAlumno.getNombreAlumno());
        datos.setNombre(entityAlumno.getMatriculaAlumno());
        datos.setEstado(entityAlumno.getEstadoAlumno());

        List<EntityAlumnoAsignatura> relaciones = entityAlumno.getListaConEntityAsignatura();
        if (relaciones != null && !relaciones.isEmpty() && relaciones.get(0).getEntityAsignaturaId() != null) {
            datos.setEntityAsignaturaId(relaciones.get(0).getEntityAsignaturaId());
        }
        return datos;
    }

    /** Verifica en Asignaturas (Feign) que exista la asignatura; null = sin relación. */
    private void validarEntityAsignatura(Long entityAsignaturaId) {
        if (entityAsignaturaId == null) {
            return;
        }
        try {
            List<EntityAsignaturaDto> encontrados = clientFeignAsignatura.obtenerDTOsDelAsignatura(List.of(entityAsignaturaId));
            boolean existe = encontrados != null && encontrados.stream()
                    .anyMatch(a -> a != null && entityAsignaturaId.equals(a.getId()));
            if (!existe) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La Asignatura #" + entityAsignaturaId + " no existe en Microservicio-Asignaturas");
            }
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo contactar al Microservicio-Asignaturas para verificar la asignatura: " + e.getMessage());
        }
    }

    /** Deja a Carrera relacionada solo con entityAsignaturaId (o sin relación si es null). */
    private void asignarEntityA(EntityAlumno entityAlumno, Long entityAsiganturaId) {
        if (entityAlumno.getListaConEntityAsignatura() == null) {
            entityAlumno.setListaConEntityAsignatura(new ArrayList<>());
        }
        entityAlumno.getListaConEntityAsignatura().clear();
        if (entityAsignaturaId != null) {
            EntityAlumnoEntityAsignatura relacion = new EntityAlumnoEntityAsignatura();
            relacion.setEntityAsignaturaId(entityAsignaturaId);
            entityAlumno.getListaConEntityAsignatura().add(relacion);
        }
    }

    @Transactional(readOnly = true)
    public List<dto> listar() {
        return repositoryAlumno.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public dto buscar(Long id) {
        EntityAlumno registro = repositoryAlumno.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado!"));
        return convertirDTO(registro);
    }

    @Transactional
    public dto crear(dto datos) {
        validarEntityAsignatura(datos.getEntityAsignaturaId());
        EntityAlumno nuevo = new EntityAlumno();
        String nombreAlumno = datos.getNombreAlumno() != null ? datos.getNombreAlumno() : datos.getNombreAlumno();
        String matricula = datos.getMatricula() != null ? datos.getMatricula() : datos.getMatricula();
        nuevo.setNombreAlumno(nombreAlumno != null ? nombreAlumno : "Sin nombre");
        nuevo.setMatricula(matricula != null ? matricula : "Sin matricula");
        nuevo.setEstadoAlumno(datos.getEstadoAlumno() != null ? datos.getEstadoAlumno() : true);
        asignarEntityAsignatura(nuevo, datos.getEntityAasignaturaId());

        EntityAlumno guardado = Alumno.save(nuevo);
        return convertirDTO(guardado);
    }

    @Transactional
    public dto actualizar(Long id, dto datos) {
        EntityAlumno registro = repositoryAlumno.findById(id)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado!"));
        validarEntityAsignatura(datos.getEntityAsignaturaId());

        if (datos.getNombreAlumno() != null || datos.getNombreAlumno() != null) {
            String nombreAlumno = datos.getNombreAlumno() != null ? datos.getNombreAlumno() : datos.getNombreAlumno();
            registro.setNombreAlumno(nombreAlumno);
        }
        if (datos.getMatricula() != null || datos.getMatricula() != null) {
            String matricula = datos.getMatricula() != null ? datos.getMatricula() : datos.getMatricula();
            registro.setMatricula(matricula);
        }
        if (datos.getEstadoAlumno() != null) {
            registro.setEstadoAlumno(datos.getEstadoAlumno());
        }
        
        asignarEntityAsignatura(registro, datos.getEntityAsignaturaId());

        EntityAlumno actualizado = repositoryAlumno.save(registro);
        return convertirDTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repositoryAlumno.existsById(id)) {
            throw new RuntimeException("Alumno no encontrado!");
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EntityAlumnoDtoList> findAll() {
        List<EntityAlumno> datosAlumno = repositoryAlumno.findAll();
        List<Long> idsRelacionados = datosAlumno.stream()
                .filter(alumno -> alumno.getListaConEntityAsignatura() != null)
                .flatMap(alumno -> alumno.getListaConEntityAsignatura().stream())
                .map(EntityAlumnoEntityAsignatura::getEntityAsignaturaId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> asignaturaMap = new HashMap<>();
        if (!idsRelacionados.isEmpty()) {
            try {
                List<EntityAsignaturaDto> listaAsignaturas = clientFeignAsignatura.obtenerDTOsDelAsignatura(idsRelacionados);
                if (listaAsignaturas != null) {
                    for (EntityAsignaturaDto asig : listaAsignaturas) {
                        if (asig != null && asig.getId() != null) {
                            asignaturaMap.put(asig.getId(), asig.getNombreAsignatura());
                        }
                    }
                }
            } catch (Exception e) {
                // En caso de que Microservicio-Asignatura no responda en ese momento
            }
        }

        return datosAlumno.stream()
                .map(e -> {
                    EntityAlumnoDtoList dtoResult = new EntityAlumnoDtoList();
                    dtoResult.setId(e.getId());
                    dtoResult.setNombreAlumno(e.getNombreAlumno());
                    dtoResult.setMatricula(e.getMatricula());
                    dtoResult.setEstadoAlumno(e.getEstadoAlumno());

                    if (e.getListaConEntityAsignatura() != null && !e.getListaConEntityAsignatura().isEmpty()) {
                        EntityAlumnoEntityAsignatura relacion = e.getListaConEntityAsigantura().get(0);
                        if (relacion != null && relacion.getEntityAsignaturaId() != null) {
                            dtoResult.setEntityAsignaturaId(relacion.getEntityAsignaturaId());
                            String nombreAsigna = asignaturaMap.get(relacion.getEntityAsignaturaId());
                            dtoResult.setNombreAsignatura(nombreAsigna != null ? nombreAsigna : "Asignatura #" + relacion.getEntityAsignaturaId());
                        } else {
                            dtoResult.setNombreAsignatura("Sin Asignatura");
                        }
                    } else {
                        dtoResult.setNombreAsignatura("Sin Asignatura");
                    }
                    return dtoResult;
                })
                .collect(Collectors.toList());
    }
}
