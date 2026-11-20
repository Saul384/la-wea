package com.microservicio.alumnos.controller;

import com.microservicio.alumnos.dto.dto;
import com.microservicio.alumnos.dto.EntityAlumnoDtoList;
import com.microservicio.alumnos.service.servicio;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alumno")
public class controllerAlumno {

    @Autowired
    private servicio servicio;

    @GetMapping
    public List<dto> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id:\\d+}")
    public dto buscar(@PathVariable Long id) {
        return servicio.buscar(id);
    }

    @PostMapping
    public dto crear(@RequestBody dto datos) {
        return servicio.crear(datos);
    }

    @PutMapping("/{id}")
    public dto actualizar(@PathVariable Long id, @RequestBody dto datos) {
        return servicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return "Alumno eliminado correctamente!";
    }

    // Consulta combinada mediante OpenFeign
    @GetMapping("/con-asignatura")
    public ResponseEntity<List<EntityAlumnoDtoList>> getAll() {
        List<EntityAlumnoDtoList> list = servicio.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/insertar-alumnos-iniciales")
    public ResponseEntity<List<dto>> insertarDatosIniciales() {
        List<dto> registros = new ArrayList<>();

        String[][] alumnosData = {
            {"Erik", "2023171006", "false"},
            {"Valeria", "2023171007", "true" },
            {"Karen", "2023171008", "true" },
            {"Emilio", "2023171009", "true" },
            {"Saul", "2023171010", "true"},
            {"Luis", "2023171011", "true"}
        };

        for (String[] alum : alumnosData) {
            dto nuevo = new dto();
            nuevo.setNombreAlumno(alum[0]);
            nuevo.setMatricula(alum[1]);
            nuevo.setEstadoAlumno(Boolean.parseBoolean(alum[2]));
            nuevo.setEntityAsignaturaId(null);
            registros.add(servicio.crear(nuevo));
        }

        return ResponseEntity.ok(registros);
    }
}
