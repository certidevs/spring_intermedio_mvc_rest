package com.certidevs.controller;

import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.http.HttpClient;

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
    void findAll() {


    }
}