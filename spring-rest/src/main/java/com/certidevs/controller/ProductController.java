package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    // Path variable products/1
    // Request param products?id=1&name=prueba
    @GetMapping("products/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {

//        Optional<Product> productOpt = productRepository.findById(id);
//        if (productOpt.isPresent())
//            return ResponseEntity.ok(productOpt.get());
//        else
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return productRepository.findById(id)
//                .map(product -> ResponseEntity.ok(product))
                // Simplificación de la lambda:
                .map(ResponseEntity::ok)
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND)); // 404 si no hay producto
    }

    @PostMapping("products/search") // Alternativa más avanzada: products?price.gte=40&price.lte=100
    public ResponseEntity<List<Product>> findAllFilteringByExample(@RequestBody Product product) {
        // Filtrados con API QBE Query By Example: https://certidevs.com/tutorial-spring-boot-query-by-example-qbe
        // Filtrado con API Specification: https://certidevs.com/tutorial-spring-boot-specification
        // Filtrado directamente sobre consultas JPQL creando un objeto y usandolo con expresiones SpEL

//        ExampleMatcher matcher = ExampleMatcher.matching()
//                .withMatcher("email", match -> match.startsWith())
//                .withIgnorePaths("manufacturer");
        var filteredProducts = productRepository.findAll(Example.of(product));
        return ResponseEntity.ok(filteredProducts);
    }

    // create
    // update
    // updatePartial
    // deleteById
    // deleteAll
}
