package com.microserviciob.microb.controller;

import com.microserviciob.microb.dto.dto;
import com.microserviciob.microb.entity.EntityBEntityA;
import com.microserviciob.microb.service.servicio;

import java.util.ArrayList;
import java.util.List;
import com.microserviciob.microb.dto.EntityBDtoList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/b")
public class controllerb {

    @Autowired
    private servicio servicio;
    @Autowired
private JdbcTemplate jdbcTemplate;

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

    @PostMapping("/insertar-iniciales")
public ResponseEntity<List<dto>> insertarDatosIniciales() {

    List<dto> registros = new ArrayList<>();

    String[] nombres = {
        "Elemento B1 Actualizado",
        "Elemento B2",
        "Elemento B3",
        "Elemento B4",
        "Elemento B5"
    };

    for (int i = 0; i < nombres.length; i++) {

        dto nuevo = new dto();
        nuevo.setNombreB(nombres[i]);

        // Aquí conserva tu lógica actual para guardar B
    }

   jdbcTemplate.batchUpdate(
    "INSERT INTO entity_b_entity_a (entity_a_id, entity_b_id) VALUES (?, ?)",
    List.of(
        new Object[]{2, 1},
        new Object[]{2, 2},
        new Object[]{2, 3},
        new Object[]{2, 4},
        new Object[]{2, 5}
    )
);

    return ResponseEntity.ok(registros);
}
    @PutMapping("/{id}")
    public dto actualizar(@PathVariable Long id, @RequestBody dto datos) {
        return servicio.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public String eliminar(@PathVariable Long id) {
        servicio.eliminar(id);
        return "Registro eliminado correctamente";
    }

    // Ruta diferenciada para obtener la lista combinada mediante OpenFeign
    @GetMapping("/con-a")
    public ResponseEntity<List<EntityBDtoList>> getAll() {
        List<EntityBDtoList> list = servicio.findAll();
        return ResponseEntity.ok(list);
    }
}