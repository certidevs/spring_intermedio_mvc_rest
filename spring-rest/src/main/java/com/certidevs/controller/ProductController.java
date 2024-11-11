package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

// http://localhost:8080/swagger-ui/index.html
@AllArgsConstructor
@RequestMapping("api")
@RestController
public class ProductController {

    private final ProductRepository productRepository;

    // http://localhost:8080/api/products
    @GetMapping("products")
    public ResponseEntity<List<Product>> findAll() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("products-iva")
    public ResponseEntity<List<Product>> findAllWithCalculatedIVA() {
//        var products = productRepository.findAll().stream().map(product -> {
//            product.setPrice(product.getPrice() * 1.21);
//            return product;
//        }).toList();

        var products = productRepository.findAll();
        products.forEach(product -> product.setPrice(product.getPrice() * 1.21));
        return ResponseEntity.ok(products);
    }
}
