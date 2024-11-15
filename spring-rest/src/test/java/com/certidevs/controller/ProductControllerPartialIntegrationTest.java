package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.http.HttpClient;
import java.util.List;

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

    

}