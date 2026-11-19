package com.microserviciob.microb.controller;

import com.microserviciob.microb.dto.dto;
import com.microserviciob.microb.dto.EntityBDtoList;
import com.microserviciob.microb.service.servicio;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/b")
public class controllerb {

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
        return "Programa educativo eliminado correctamente";
    }

    // Consulta combinada mediante OpenFeign
    @GetMapping("/con-a")
    public ResponseEntity<List<EntityBDtoList>> getAll() {
        List<EntityBDtoList> list = servicio.findAll();
        return ResponseEntity.ok(list);
    }

    @PostMapping("/insertar-iniciales")
    public ResponseEntity<List<dto>> insertarDatosIniciales() {
        List<dto> registros = new ArrayList<>();

        String[][] programasData = {
            {"Ingeniería en Desarrollo y Gestión de Software", "Enfoque en desarrollo web, arquitecturas cloud y microservicios.", "false"},
            {"Ingeniería en Redes y Ciberseguridad", "Protección de infraestructura crítica y administración de redes.", "false"},
            {"Ingeniería Industrial y Sistemas Productivos", "Optimización de procesos productivos y manufactura esbelta.", "true"},
            {"Licenciatura en Negocios y Mercadotecnia", "Estrategias de comercialización, branding y ventas digitales.", "false"},
            {"Ingeniería Mecatrónica", "Automatización, robótica industrial y sistemas embebidos.", "true"}
        };

        for (String[] prog : programasData) {
            dto nuevo = new dto();
            nuevo.setNombre(prog[0]);
            nuevo.setDescripcion(prog[1]);
            nuevo.setIntensiva(Boolean.parseBoolean(prog[2]));
            nuevo.setEstado(true);
            nuevo.setPublica(true);
            nuevo.setEntityAId(null);
            registros.add(servicio.crear(nuevo));
        }

        return ResponseEntity.ok(registros);
    }
}
