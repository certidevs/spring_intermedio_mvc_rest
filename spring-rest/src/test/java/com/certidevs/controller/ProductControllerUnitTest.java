package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/*
Test unitario, NO carga Spring
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerUnitTest {

    // dependencias
    @Mock
    private ProductRepository productRepository;

    // SUT - System Under Test
    @InjectMocks
    private ProductController productController;


    @Test
    @DisplayName("Buscar todos los productos")
    void findAll() {
        // 1. configurar datos fixture y mock
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").build(),
                Product.builder().id(2L).name("prod2").build()
        );
        when(productRepository.findAll()).thenReturn(products);

        // 2. invocar el metodo a testear
        var response = productController.findAll();

        // 3. asserts y verificaciones
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(products, response.getBody());
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Buscar todos los productos con IVA calculado")
    void findAllWithCalculatedIVA() {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(100d).build()
        );
        when(productRepository.findAll()).thenReturn(products);

        // 2. invocar el metodo a testear
        var response = productController.findAllWithCalculatedIVA();

        // 3. asserts y verificaciones
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(products, response.getBody());
        assertEquals(12.1d, response.getBody().getFirst().getPrice());
        assertEquals(121d, response.getBody().getLast().getPrice());
    }

    @Test
    void findById_Found() {


    }

    @Test
    void findById_NotFound() {

    }


}