package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
MYSQL con TestContainers
H2 Memoria
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Transactional se usa para deshacer los cambios introducidos por cada test cuando termina
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // NO ES UN MOCK: inyectar repositorio real ProductRepository
    @Autowired
    private ProductRepository productRepository;

//    @BeforeEach
//    void setUp() {
//        // Se ejecuta antes de cada test para asegurarnos de empezar con un entorno limpio
//        productRepository.deleteAllInBatch();
//    }
//    @AfterEach
//    void tearDown() {
//
//    }

    @Test
    void findAll() throws Exception {
        productRepository.saveAll(List.of(
           Product.builder().name("prod1").price(30d).active(true).build(),
           Product.builder().name("prod2").price(30d).active(true).build(),
           Product.builder().name("prod3").price(30d).active(true).build()
        ));
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("prod1"))
                .andExpect(jsonPath("$[1].name").value("prod2"))
                .andExpect(jsonPath("$[0].active").value(true));

        productRepository.findAll().forEach(System.out::println);
    }

    @Test
    void findAllWithCalculatedIVA()throws Exception {
        productRepository.saveAll(List.of(
                Product.builder().name("prod1").price(10d).active(true).build(),
                Product.builder().name("prod2").price(100d).active(true).build()
        ));
        mockMvc.perform(get("/api/products-iva"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price").value(12.1d))
                .andExpect(jsonPath("$[1].price").value(121d));

        productRepository.findAll().forEach(System.out::println);
    }

}
