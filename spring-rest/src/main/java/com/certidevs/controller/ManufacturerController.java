package com.certidevs.controller;


import com.certidevs.model.Manufacturer;
import com.certidevs.model.Product;
import com.certidevs.repository.ManufacturerRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@RequestMapping("api")
@RestController
public class ManufacturerController {

    private final ManufacturerRepository manufacturerRepository;

    @GetMapping("manufacturers")
    public ResponseEntity<List<Manufacturer>> findAll() {
        return ResponseEntity.ok(manufacturerRepository.findAll());
    }

    // Si hay productos lanza una segunda Query para traer los productos
    @GetMapping("manufacturers/{id}")
    public ResponseEntity<Manufacturer> findById(@PathVariable Long id) {
        return manufacturerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND)); // 404 si no hay fabricante
    }

    // Trae los productos ya por defecto en una sola query junto con el manufacturer
    @GetMapping("manufacturers-with-products/{id}")
    public ResponseEntity<Manufacturer> findByIdWithProducts(@PathVariable Long id) {
        return manufacturerRepository.findByIdEager(id)
                .map(ResponseEntity::ok)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND)); // 404 si no hay fabricante
    }


}
