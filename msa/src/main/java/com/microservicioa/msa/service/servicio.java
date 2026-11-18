package com.microservicioa.msa.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.microservicioa.msa.dto.dto;
import com.microservicioa.msa.entity.entidad;
import com.microservicioa.msa.repository.repositorya;

@Service
public class servicio {

    @Autowired
    repositorya repository;

    // Convierte una entidad en DTO
    public dto convertirDTO(entidad entidad) {
        dto datos = new dto();

        datos.setId(entidad.getId());
        datos.setNombreA(entidad.getNombreA());

        return datos;
    }

    // Consulta todos los registros
    public List<dto> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    // Consulta un registro por su ID
    public dto buscar(Long id) {
        entidad registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        return convertirDTO(registro);
    }

    // Crea un nuevo registro
    public dto crear(dto datos) {
        entidad nuevo = new entidad();

        nuevo.setNombreA(datos.getNombreA());

        entidad guardado = repository.save(nuevo);

        return convertirDTO(guardado);
    }

    // Actualiza un registro existente
    public dto actualizar(Long id, dto datos) {
        entidad registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Registro no encontrado"));

        registro.setNombreA(datos.getNombreA());

        entidad actualizado = repository.save(registro);

        return convertirDTO(actualizado);
    }

    // Elimina un registro por su ID
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Registro no encontrado");
        }

        repository.deleteById(id);
    }
    @Transactional(readOnly = true)
public List<dto> findAllByIDs(List<Long> ids) {
    if (ids == null || ids.isEmpty()) {
        return List.of();
    }
    return repository.findAllById(ids)
            .stream()
            .map(this::convertirDTO)
            .collect(Collectors.toList());
}
}