package com.microserviciob.microb.service;

import com.microserviciob.microb.client.ClientFeignMSA;
import com.microserviciob.microb.dto.EntityADto;
import com.microserviciob.microb.dto.EntityBDtoList;
import com.microserviciob.microb.dto.dto;
import com.microserviciob.microb.entity.EntityB;
import com.microserviciob.microb.entity.EntityBEntityA;
import com.microserviciob.microb.repository.repositoryb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        datos.setNombreB(entity.getNombreB());
        return datos;
    }

    public List<dto> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    public dto buscar(Long id) {
        EntityB registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        return convertirDTO(registro);
    }

    public dto crear(dto datos) {
        EntityB nuevo = new EntityB();
        nuevo.setNombreB(datos.getNombreB());
        EntityB guardado = repository.save(nuevo);
        return convertirDTO(guardado);
    }

    public dto actualizar(Long id, dto datos) {
        EntityB registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));
        registro.setNombreB(datos.getNombreB());
        EntityB actualizado = repository.save(registro);
        return convertirDTO(actualizado);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro no encontrado");
        }
        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EntityBDtoList> findAll() {
        List<EntityB> datosB = repository.findAll();
        List<Integer> idsRelacionados = datosB.stream()
                .filter(relacion -> relacion.getListaConEntityA() != null)
                .flatMap(relacion -> relacion.getListaConEntityA().stream())
                .map(EntityBEntityA::getEntityAId)
                .collect(Collectors.toList());

        Map<Integer, String> entidadAIdNombreAMap = new HashMap<>();
        if (!idsRelacionados.isEmpty()) {
            List<EntityADto> listaEntityADtos = clientFeignMSA.obtenerDTOsDelMSA(idsRelacionados);
            if (listaEntityADtos != null) {
                for (EntityADto entityDto : listaEntityADtos) {
                    if (entityDto != null) {
                        entidadAIdNombreAMap.put(entityDto.getId(), entityDto.getNombreA());
                    }
                }
            }
        }

        List<EntityBDtoList> datosList = datosB.stream()
                .map(e -> {
                    EntityBDtoList dtoResult = new EntityBDtoList();
                    dtoResult.setId(e.getId());
                    dtoResult.setNombreB(e.getNombreB());
                    if (e.getListaConEntityA() != null && !e.getListaConEntityA().isEmpty()) {
                        EntityBEntityA relacion = e.getListaConEntityA().get(0);
                        if (relacion != null && relacion.getEntityAId() != null) {
                            String nombreA = entidadAIdNombreAMap.get(relacion.getEntityAId());
                            dtoResult.setNombreA(nombreA != null ? nombreA : "Sin dato de MSA");
                        } else {
                            dtoResult.setNombreA("Sin dato de MSA");
                        }
                    } else {
                        dtoResult.setNombreA("Sin dato de MSA");
                    }
                    return dtoResult;
                })
                .collect(Collectors.toList());

        return datosList;
    }
}