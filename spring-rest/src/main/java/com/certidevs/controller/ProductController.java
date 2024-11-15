package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
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
        // Puede producir NullPointerException si los price son null:
        // products.forEach(product -> product.setPrice(product.getPrice() * 1.21));

        products.forEach(product -> {
            if(product.getPrice() != null) product.setPrice(product.getPrice() * 1.21);
        });
//        products.forEach(product ->
//           product.setPrice(Optional.ofNullable(product.getPrice()).orElse(0d) * 1.21)
//        );
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
    @PostMapping("products")
    // Si queremos transaccionalidad con rollback, ideal moverlo a un servicio
    // @Transactional(rollbackFor = DataIntegrityViolationException.class)
    public ResponseEntity<Product> create(@RequestBody Product product) {
        if (product.getId() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST); // 400 el producto no puede tener ID

        // product.setAuthor(SecurityUtils.getCurrentUser())
        // product.setCreationDate(LocalDateTime.now())

        // if (product.getManufacturer().getId() == null)
        //    manufacturerRepository.save(product.getManufacturer())

          // var url = fileService.save(file)
        // product.setImage(url);

        try {
            productRepository.save(product);
            return ResponseEntity.created(new URI("/api/products/" + product.getId())).body(product);
            // return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT); // 409 no se puede guardar porque genera conflicto
        }
    }

    // IDEAL PRIMERO HACER UN FIND BY ID PARA TENER EL OBJETO ENTERO ANTES DE ACTUALIZARLO Y ENVIARLO AQUÍ
    @PutMapping("products/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        if (! productRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // findById productDB  setName, setPrice
        try {
            productRepository.save(product);
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PatchMapping("products/{id}")
    public ResponseEntity<Product> updatePartial(@PathVariable Long id, @RequestBody Product product) {
        if (! productRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        // Modificar solo aquellos atributos que nos envíen que no sean null:
        return productRepository.findById(id).map(productDB -> {
            if (StringUtils.hasText(product.getName())) productDB.setName(product.getName());
            if (product.getPrice() != null) productDB.setPrice(product.getPrice());
            if (product.getQuantity() != null) productDB.setQuantity(product.getQuantity());
            productRepository.save(productDB);
            return ResponseEntity.ok(productDB);
            // BeanUtils.copyProperties();
            // mapStruct con mapper
        }).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("products/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        // opción 1: borrar sin comprobar nada:
        try {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // 204
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }

        // Opción 2: desasociaciones si tenemos Entidades apuntando a Product: purchase, rating, invoice, favorite
        // borrar directamente lo que apunta a product (extrema)
        // purchaseRepository.deleteByProductId(id)
        // desasociar
        // purchaseRepository.updateSetProductToNullByProductId(id)

        // Opción 3: desactivar el producto pero no borrar ni desasociar nada:
//        productRepository.findById(id).map(productDB -> {
//            productDB.setActive(false);
//            productRepository.save(productDB);
//            return productDB;
//        });
//        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("products/disable/{id}")
    public ResponseEntity<Void> disableProduct(@PathVariable Long id) {
        productRepository.findById(id).map(productDB -> {
            productDB.setActive(false);
            productRepository.save(productDB);
            return productDB;
        });
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("products")
    public ResponseEntity<Void> deleteAllByIds(@RequestBody List<Long> ids) {
        try {
            productRepository.deleteAllByIdInBatch(ids);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }
}
