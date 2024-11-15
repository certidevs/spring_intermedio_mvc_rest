package com.certidevs.controller;

import com.certidevs.model.Product;
import com.certidevs.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    @Autowired
    private ObjectMapper objectMapper;

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
    void findAllWithCalculatedIVA() throws Exception {
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

    @Test
    void findAll_QBE() throws Exception {
        productRepository.saveAll(List.of(
                Product.builder().name("prod1").price(10d).active(true).build(),
                Product.builder().name("prod2").price(20d).active(true).build(),
                Product.builder().name("prod2").price(20d).active(true).build(),
                Product.builder().name("prod2").price(30d).active(true).build()
        ));

        var productFilter = Product.builder().price(20d).build();

        mockMvc.perform(post("/api/products/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productFilter))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].price").value(20d))
                .andExpect(jsonPath("$[1].price").value(20d));
    }

    @Test
    void create_OK()  throws Exception{
        var product = Product.builder().name("prod1").price(20d).build();

        MvcResult result = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("prod1"))
                .andExpect(jsonPath("$.price").value(20d))
                .andReturn();

        String location = result.getResponse().getHeader("location");
        assertNotNull(location); // "/api/products/1"

        // Extraer el id del location
        Long locationId = Long.valueOf(location.substring(location.lastIndexOf("/") + 1));
        //Long locationId = Long.valueOf(location.split("/")[location.split("/").length - 1]);

        // Extraer el id del la respuesta JSON
        Product productResponse = objectMapper.readValue(result.getResponse().getContentAsString(), Product.class);
        assertEquals(locationId, productResponse.getId());

        mockMvc.perform(get(location))
                .andExpect(jsonPath("$.id").value(productResponse.getId()));

        assertTrue(productRepository.existsById(locationId));
        // productRepository.findAll().get(0)
        // productRepository.findById(id).get()
    }

    @Test
    void update_OK() throws Exception{
        var product = Product.builder().name("prod1").price(20d).build();
        productRepository.save(product);

        product.setName("prod1 edit");
        product.setPrice(30d);

        mockMvc.perform(put("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("prod1 edit"))
                .andExpect(jsonPath("$.price").value(30d));

        var productSaved = productRepository.findById(product.getId()).get();
        assertEquals("prod1 edit", productSaved.getName());
        assertEquals(30d, productSaved.getPrice());
    }

    @Test
    void deleteById_OK() throws Exception {
        var product = Product.builder().name("prod1").price(20d).build();
        productRepository.save(product);

        mockMvc.perform(delete("/api/products/{id}", product.getId())
                )
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").doesNotExist());

        assertFalse(productRepository.existsById(product.getId()));
    }

}
