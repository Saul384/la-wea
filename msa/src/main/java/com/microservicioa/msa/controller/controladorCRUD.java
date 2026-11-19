package com.microservicioa.msa.controller;

import com.microservicioa.msa.dto.dto;
import com.microservicioa.msa.service.servicio;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/entity-a")
public class controladorCRUD {

    @Autowired
    private servicio servicio;

    @GetMapping
    public List<dto> listar() {
        return servicio.listar();
    }

    @GetMapping("/{id}")
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
        return "División eliminada correctamente";
    }

    @PostMapping("/by-ids")
    public ResponseEntity<List<dto>> getAllByIDs(@RequestBody List<Long> ids) {
        List<dto> list = servicio.findAllByIDs(ids);
        return ResponseEntity.ok(list);
    }
}
