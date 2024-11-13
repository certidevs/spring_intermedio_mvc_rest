package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

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
    @DisplayName("Buscar producto por id - Sí encontrado")
    void findById_Found() {
        var product = Product.builder().id(1L).name("prod1").price(10d).build();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        var response = productController.findById(1L);

        // asserts de JUnit
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        assertEquals(product.getId(), response.getBody().getId());

        // verificaciones de mockito, interacciones con los mocks:
        verify(productRepository).findById(1L);
    }

    @Test
    @DisplayName("Buscar producto por id - NO encontrado")
    void findById_NotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        var exception = assertThrows(
                ResponseStatusException.class,
                () -> productController.findById(3L)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }


    @Test
    @DisplayName("Buscar producto con filtro Query By Example")
    void findAllFilteringByExample() {

        var product = Product.builder().price(10d).build();
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(100d).build()
        );
        // when(productRepository.findAll((Example<Product>) any())).thenReturn(products);
        when(productRepository.findAll(ArgumentMatchers.<Example<Product>>any())).thenReturn(products);

        var response = productController.findAllFilteringByExample(product);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(products, response.getBody());
        verify(productRepository).findAll(ArgumentMatchers.<Example<Product>>any());

    }

    @Test
    @DisplayName("Crear producto nuevo - No debe tener id - Bad Request")
    void create_badRequest() {
        Product product = Product.builder().id(1L).build();
        var exception = assertThrows(ResponseStatusException.class, () -> productController.create(product));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    @DisplayName("Crear producto nuevo - OK")
    void create_ok() {

    }

    @Test
    @DisplayName("Crear producto nuevo - Conflicto por excepción de repositorio")
    void create_conflict () {

    }


}