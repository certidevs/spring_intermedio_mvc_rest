package com.certidevs.controller;


import com.certidevs.model.Manufacturer;
import com.certidevs.model.Product;
import com.certidevs.repository.ManufacturerRepository;
import com.certidevs.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RequestMapping("api")
@RestController
public class ManufacturerController {

    private final ManufacturerRepository manufacturerRepository;
    private final ProductRepository productRepository;

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

    @PostMapping("manufacturers")
    public ResponseEntity<Manufacturer> create(@RequestBody Manufacturer manufacturer) {
        if (manufacturer.getId() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 400 el producto no puede tener ID

        try {
            manufacturerRepository.save(manufacturer);

             // OPCIONAL: para guardar asociacion productos desde aquí:
            var ids = manufacturer.getProducts().stream().map(product -> product.getId()).toList();
            productRepository.updateManufacturerByIdIn(manufacturer, ids);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(manufacturer);

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT); // 409 no se puede guardar porque genera conflicto
        }
    }

    @DeleteMapping("manufacturers/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            manufacturerRepository.findById(id).map(manufacturer -> {
                // desasociar/borrar aquellas entidades que apunten al fabricante
                // productRepository.deleteByManufacturer(manufacturer);
                productRepository.updateSetManufacturerToNullByManufacturerId(id);
                manufacturerRepository.deleteById(id);
                return manufacturer;
            });
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

    }


}
