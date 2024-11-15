package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.http.HttpClient;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/*
Test de integración parcial
Carga Spring, pero solo mínimo necesario para ejecutar la parte del controlador
NO carga la base de datos
Sí se usan mocks para las dependencias: servicios, repositorios
La idea es poder testear las rutas y parámetros y ejecución de Spring sobre los controlaadores
 */
@WebMvcTest(ProductController.class)
class ProductControllerPartialIntegrationTest {

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Buscar productos - sí hay productos")
    void findAll() throws Exception {

        // Parte 1: mocks igual que en los tests unitarios
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").build(),
                Product.builder().id(2L).name("prod2").build(),
                Product.builder().id(3L).name("prod3").build()
        );
        when(productRepository.findAll()).thenReturn(products);

        // Parte 2: invocar el metodo a testear
        // ya no invocamos directamente el metodo a testear
        // Se lanza una petición HTTP a la ruta
        // TestRestTemplate
        // HttpClient
        // WebClient
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("prod1"))
                .andExpect(jsonPath("$[1].name").value("prod2"))
                .andExpect(jsonPath("$[0].active").doesNotExist());

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Buscar productos - no hay productos")
    void findAll_Empty() throws Exception {
        when(productRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());

        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Buscar productos con IVA - OK")
    void findAllWithIVA_OK() throws Exception {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(100d).build()
        );
        when(productRepository.findAll()).thenReturn(products);

        mockMvc.perform(get("/api/products-iva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price").value(12.1d))
                .andExpect(jsonPath("$[1].price").value(121d));
        verify(productRepository).findAll();
    }

    @Test
    @DisplayName("Buscar productos con IVA - precios nulos")
    void findAllWithIVA_NullPrice() throws Exception {
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").build(),
                Product.builder().id(2L).name("prod2").build()
        );
        when(productRepository.findAll()).thenReturn(products);

        // Hemos detectado NullPointerException y lo hemos solucionado
        mockMvc.perform(get("/api/products-iva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].price").doesNotExist())
                .andExpect(jsonPath("$[1].price").isEmpty());
    }

    @Test
    @DisplayName("Buscar todos los productos filtrando - Query By Example")
    void findAll_QBE() throws Exception {
        // lista de productos originales que habría en base de datos:
        List<Product> products = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build(),
                Product.builder().id(2L).name("prod2").price(100d).build()
        );
        // Filtrar por precio 10 €
        var productFilter = Product.builder().price(10d).build();
        List<Product> productsFiltered = List.of(
                Product.builder().id(1L).name("prod1").price(10d).build()
        );
        when(productRepository.findAll(ArgumentMatchers.<Example<Product>>any())).thenReturn(productsFiltered);

        // Convertir el objeto Java Product a string json para enviarlo al controlador (usa Jackson para la conversión)
        String productFilterJson = convertToJson(productFilter);

        // java.lang.IllegalArgumentException: 'url' should start with a path or be a complete HTTP URL: api/products/search
//        mockMvc.perform(post("api/products/search")
        mockMvc.perform(post("/api/products/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productFilterJson)
//                        .content("""
//                          {
//                            "price": 10
//                          }
//                          """)
                )
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].price").value(10d));
        verify(productRepository).findAll(ArgumentMatchers.<Example<Product>>any());

    }

    @Test
    @DisplayName("Buscar producto por id - OK")
    void findById_OK() throws Exception {
        var product = Product.builder().id(1L).name("prod1").price(10d).build();
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("prod1"))
                .andExpect(jsonPath("$.price").value(10d));

        verify(productRepository).findById(product.getId());
    }

    @Test
    @DisplayName("Buscar producto por id - Not found")
    void findById_NotFound() throws Exception {

        Long id = 1L;

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());

        verify(productRepository).findById(id);
    }

    @Test
    @DisplayName("Crear producto - OK")
    void create_OK() throws Exception {

        Product product = Product.builder()
                .name("prod1").price(33.3).build();

        String productJson = convertToJson(product);

        // CUIDADO: ponemos any(Product.class) para que admita cualquier referencia de product, porque si dejamos el product del test no lo detectará
        // ya que en el controller la referencia al product será distinta es un nuevo objeto creado por jackson
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product productArgument = invocation.getArgument(0);
            productArgument.setId(1L); // Simular que la DB le asigna un Id
            return productArgument;
        });

        mockMvc.perform(
                post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(productJson)
        ).andExpect(status().isCreated())
        .andExpect(header().exists("location"))
        .andExpect(header().string("location", "/api/products/1"))
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.price").value(product.getPrice()));

        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Crear producto - con id genera bad request")
    void create_BadRequest() throws Exception {
        Product product = Product.builder()
                .id(1L) // CUIDADO: asignamos un id para forzar el bad request
                .name("prod1").price(33.3).build();

        String productJson = convertToJson(product);

        mockMvc.perform(
                        post("/api/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(productJson)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").doesNotExist());

        verify(productRepository, never()).save(any(Product.class));
    }
    @Test
    @DisplayName("Actualizar producto - OK")
    void update_OK() throws Exception {

        Product product = Product.builder().id(1L).name("prod1").price(30d).build();
        when(productRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(
                    put("/api/products/{id}", product.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(product))
            ).andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(product.getId()))
            .andExpect(jsonPath("$.name").value("prod1"))
            .andExpect(jsonPath("$.price").value(30d));

        // CUIDADO: puede fallar porque son referencias en memoria diferente:
        // verify(productRepository).save(product);
        verify(productRepository).save(any(Product.class));

    }

    @Test
    @DisplayName("Actualizar producto - conflict")
    void update_Conflict() throws Exception {
        Product product = Product.builder().id(1L).name("prod1").price(30d).build();

        when(productRepository.existsById(1L)).thenReturn(true);

        when(productRepository.save(any(Product.class))).thenThrow(new DataIntegrityViolationException("Conflict"));

        mockMvc.perform(
                        put("/api/products/{id}", product.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(product))
                ).andExpect(status().isConflict());
    }





    private String convertToJson(Product product) throws JsonProcessingException {
        return objectMapper.writeValueAsString(product);
    }


}