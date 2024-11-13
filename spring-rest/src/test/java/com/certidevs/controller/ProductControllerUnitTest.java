package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/*
Test unitario, NO carga Spring
https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
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
        Product product = Product.builder().name("prod1").build();

        when(productRepository.save(product)).thenAnswer(invocation -> {
            Product productArgument = invocation.getArgument(0);
            productArgument.setId(1L); // Simular que la DB le asigna un Id
            return productArgument;
        });

        // assert de: status, headers y body
        var response = productController.create(product); // tiene id null
        assertNotNull(response);
        assertNotNull(response.getHeaders());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("/api/products/1", response.getHeaders().getLocation().toString());
        assertEquals(1, response.getBody().getId());
        assertEquals("prod1", response.getBody().getName());

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Crear producto nuevo - Conflicto por excepción de repositorio")
    void create_conflict () {
        Product product = Product.builder().name("prod1").build();

        // Simular que la base de datos (repositorio) lanza una excepción
        // otros casos ejemplo: leer un archivo, lanzar peticiones HTTP a otros microservicios/apirest, enviar correo por smtp....
        when(productRepository.save(product)).thenThrow(new DataIntegrityViolationException("Conflict"));

        var exception = assertThrows(ResponseStatusException.class,
                () -> productController.create(product)
        );
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    @DisplayName("Actualizar producto existente - No encontrado")
    void update_notFound() {
        Product product = Product.builder().id(1L).name("prod1").build();
        when(productRepository.existsById(1L)).thenReturn(false);

        var exception = assertThrows(ResponseStatusException.class,
                () -> productController.update(1L, product)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    @DisplayName("Actualizar producto existente - OK")
    void update_ok() {
        Product product = Product.builder().id(1L).name("prod1").build();
        when(productRepository.existsById(1L)).thenReturn(true);

        var response = productController.update(1L , product);

        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(product, response.getBody());
        verify(productRepository).save(product);
        verify(productRepository, never()).findById(anyLong());
    }
    @Test
    void update_conflict() {
        Product product = Product.builder().id(1L).name("prod1").build();

        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.save(product)).thenThrow(new DataIntegrityViolationException("Conflict"));

        var exception = assertThrows(ResponseStatusException.class,
                () -> productController.update(1L, product)
        );
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void updatePartial_notFound() {
        Product product = Product.builder().id(1L).name("prod1").build();
        when(productRepository.existsById(1L)).thenReturn(false);

        var exception = assertThrows(ResponseStatusException.class,
                () -> productController.updatePartial(1L, product)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }



    @Test
    void updatePartial_OK() {
        Product productOriginal = Product.builder().id(1L).name("prod1").price(20d).active(true).build();
        Product productEdited = Product.builder().id(1L).name("prod1 edit").price(50d).active(false).build();

        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.of(productOriginal));

        var response = productController.updatePartial(1L, productEdited);
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Comprobar que se ha editado correctamente name, price, quantity
        assertEquals("prod1 edit", response.getBody().getName());
        assertEquals(50d, response.getBody().getPrice());
        // CUIDADO: verificamos que se conserva el valor true de active
        assertEquals(true, response.getBody().getActive());
        assertTrue(response.getBody().getActive());
    }

    @Test
    void updatePartial_findByIdNotFound() {
        Product product = Product.builder().id(1L).name("prod1").build();

        when(productRepository.existsById(1L)).thenReturn(true);
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        var exception = assertThrows(ResponseStatusException.class,
                () -> productController.updatePartial(1L, product)
        );
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

}