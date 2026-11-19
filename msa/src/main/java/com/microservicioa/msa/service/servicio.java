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
    private repositorya repository;

    // Convierte una entidad en DTO
    public dto convertirDTO(entidad entidad) {
        dto datos = new dto();
        datos.setId(entidad.getId());
        datos.setNombre(entidad.getNombre());
        datos.setEstado(entidad.getEstado());
        datos.setPublica(entidad.getPublica());
        datos.setDescripcion(entidad.getDescripcion());
        return datos;
    }

    // Consulta todos los registros
    @Transactional(readOnly = true)
    public List<dto> listar() {
        return repository.findAll()
                .stream()
                .map(this::convertirDTO)
                .collect(Collectors.toList());
    }

    // Consulta un registro por su ID
    @Transactional(readOnly = true)
    public dto buscar(Long id) {
        entidad registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("División no encontrada"));

        return convertirDTO(registro);
    }

    // Crea un nuevo registro
    @Transactional
    public dto crear(dto datos) {
        entidad nuevo = new entidad();
        String nombre = datos.getNombre() != null ? datos.getNombre() : datos.getNombreA();
        nuevo.setNombre(nombre != null ? nombre : "Sin nombre");
        nuevo.setEstado(datos.getEstado() != null ? datos.getEstado() : true);
        nuevo.setPublica(datos.getPublica() != null ? datos.getPublica() : true);
        nuevo.setDescripcion(datos.getDescripcion());

        entidad guardado = repository.save(nuevo);
        return convertirDTO(guardado);
    }

    // Actualiza un registro existente
    @Transactional
    public dto actualizar(Long id, dto datos) {
        entidad registro = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("División no encontrada"));

        if (datos.getNombre() != null || datos.getNombreA() != null) {
            String nombre = datos.getNombre() != null ? datos.getNombre() : datos.getNombreA();
            registro.setNombre(nombre);
        }
        if (datos.getEstado() != null) {
            registro.setEstado(datos.getEstado());
        }
        if (datos.getPublica() != null) {
            registro.setPublica(datos.getPublica());
        }
        if (datos.getDescripcion() != null) {
            registro.setDescripcion(datos.getDescripcion());
        }

        entidad actualizado = repository.save(registro);
        return convertirDTO(actualizado);
    }

    // Elimina un registro por su ID
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("División no encontrada");
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
