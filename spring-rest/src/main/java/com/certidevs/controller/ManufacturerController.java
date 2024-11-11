package com.certidevs.controller;


import com.certidevs.model.Manufacturer;
import com.certidevs.repository.ManufacturerRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
