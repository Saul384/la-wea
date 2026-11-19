package com.microserviciob.microb.service;

import com.microserviciob.microb.client.ClientFeignMSA;
import com.microserviciob.microb.dto.EntityADto;
import com.microserviciob.microb.dto.EntityBDtoList;
import com.microserviciob.microb.dto.dto;
import com.microserviciob.microb.entity.EntityB;
import com.microserviciob.microb.entity.EntityBEntityA;
import com.microserviciob.microb.repository.repositoryb;
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
    private repositoryb repository;

    @Autowired
    private ClientFeignMSA clientFeignMSA;

    public dto convertirDTO(EntityB entity) {
        dto datos = new dto();
        datos.setId(entity.getId());
        datos.setNombre(entity.getNombre());
        datos.setEstado(entity.getEstado());
        datos.setPublica(entity.getPublica());
        datos.setIntensiva(entity.getIntensiva());
        datos.setDescripcion(entity.getDescripcion());

        List<EntityBEntityA> relaciones = entity.getListaConEntityA();
        if (relaciones != null && !relaciones.isEmpty() && relaciones.get(0).getEntityAId() != null) {
            datos.setEntityAId(relaciones.get(0).getEntityAId());
        }
        return datos;
    }

    /** Verifica en MS-A (Feign) que exista la división; null = sin relación. */
    private void validarEntityA(Long entityAId) {
        if (entityAId == null) {
            return;
        }
        try {
            List<EntityADto> encontrados = clientFeignMSA.obtenerDTOsDelMSA(List.of(entityAId));
            boolean existe = encontrados != null && encontrados.stream()
                    .anyMatch(a -> a != null && entityAId.equals(a.getId()));
            if (!existe) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "La División #" + entityAId + " no existe en MS-A");
            }
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo contactar a MS-A para verificar la división: " + e.getMessage());
        }
    }

    /** Deja a Carrera relacionada solo con entityAId (o sin relación si es null). */
    private void asignarEntityA(EntityB entity, Long entityAId) {
        if (entity.getListaConEntityA() == null) {
            entity.setListaConEntityA(new ArrayList<>());
        }
        entity.getListaConEntityA().clear();
        if (entityAId != null) {
            EntityBEntityA relacion = new EntityBEntityA();
            relacion.setEntityAId(entityAId);
            entity.getListaConEntityA().add(relacion);
        }
    }

    @Transactional(readOnly = true)
    public List<dto> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public dto buscar(Long id) {
        EntityB registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Programa educativo no encontrado"));
        return convertirDTO(registro);
    }

    @Transactional
    public dto crear(dto datos) {
        validarEntityA(datos.getEntityAId());
        EntityB nuevo = new EntityB();
        String nombre = datos.getNombre() != null ? datos.getNombre() : datos.getNombreB();
        nuevo.setNombre(nombre != null ? nombre : "Sin nombre");
        nuevo.setEstado(datos.getEstado() != null ? datos.getEstado() : true);
        nuevo.setPublica(datos.getPublica() != null ? datos.getPublica() : true);
        nuevo.setIntensiva(datos.getIntensiva() != null ? datos.getIntensiva() : false);
        nuevo.setDescripcion(datos.getDescripcion());
        asignarEntityA(nuevo, datos.getEntityAId());

        EntityB guardado = repository.save(nuevo);
        return convertirDTO(guardado);
    }

    @Transactional
    public dto actualizar(Long id, dto datos) {
        EntityB registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carrera no encontrada"));
        validarEntityA(datos.getEntityAId());

        if (datos.getNombre() != null || datos.getNombreB() != null) {
            String nombre = datos.getNombre() != null ? datos.getNombre() : datos.getNombreB();
            registro.setNombre(nombre);
        }
        if (datos.getEstado() != null) {
            registro.setEstado(datos.getEstado());
        }
        if (datos.getPublica() != null) {
            registro.setPublica(datos.getPublica());
        }
        if (datos.getIntensiva() != null) {
            registro.setIntensiva(datos.getIntensiva());
        }
        if (datos.getDescripcion() != null) {
            registro.setDescripcion(datos.getDescripcion());
        }
        asignarEntityA(registro, datos.getEntityAId());

        EntityB actualizado = repository.save(registro);
        return convertirDTO(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Programa educativo no encontrado");
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EntityBDtoList> findAll() {
        List<EntityB> datosB = repository.findAll();
        List<Long> idsRelacionados = datosB.stream()
                .filter(carrera -> carrera.getListaConEntityA() != null)
                .flatMap(carrera -> carrera.getListaConEntityA().stream())
                .map(EntityBEntityA::getEntityAId)
                .filter(id -> id != null)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> divisionMap = new HashMap<>();
        if (!idsRelacionados.isEmpty()) {
            try {
                List<EntityADto> listaDivisiones = clientFeignMSA.obtenerDTOsDelMSA(idsRelacionados);
                if (listaDivisiones != null) {
                    for (EntityADto div : listaDivisiones) {
                        if (div != null && div.getId() != null) {
                            divisionMap.put(div.getId(), div.getNombre());
                        }
                    }
                }
            } catch (Exception e) {
                // En caso de que MS-A no responda en ese momento
            }
        }

        return datosB.stream()
                .map(e -> {
                    EntityBDtoList dtoResult = new EntityBDtoList();
                    dtoResult.setId(e.getId());
                    dtoResult.setNombre(e.getNombre());
                    dtoResult.setEstado(e.getEstado());
                    dtoResult.setPublica(e.getPublica());
                    dtoResult.setIntensiva(e.getIntensiva());
                    dtoResult.setDescripcion(e.getDescripcion());

                    if (e.getListaConEntityA() != null && !e.getListaConEntityA().isEmpty()) {
                        EntityBEntityA relacion = e.getListaConEntityA().get(0);
                        if (relacion != null && relacion.getEntityAId() != null) {
                            dtoResult.setEntityAId(relacion.getEntityAId());
                            String nombreDiv = divisionMap.get(relacion.getEntityAId());
                            dtoResult.setNombreDivision(nombreDiv != null ? nombreDiv : "División #" + relacion.getEntityAId());
                        } else {
                            dtoResult.setNombreDivision("Sin división");
                        }
                    } else {
                        dtoResult.setNombreDivision("Sin división");
                    }
                    return dtoResult;
                })
                .collect(Collectors.toList());
    }
}
