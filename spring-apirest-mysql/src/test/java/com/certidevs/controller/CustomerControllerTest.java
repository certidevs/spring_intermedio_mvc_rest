package com.certidevs.controller;

import com.certidevs.model.Customer;
import com.certidevs.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void findAll() throws Exception {

        customerRepository.saveAll(List.of(
                Customer.builder().name("customer 1").email("c1@gmail.com").salary(1000d).build(),
                Customer.builder().name("customer 2").email("c2@gmail.com").salary(1000d).build(),
                Customer.builder().name("customer 3").email("c3@gmail.com").salary(1000d).build()
        ));

        mockMvc.perform(
                get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("customer 1"))
                .andExpect(jsonPath("$[1].name").value("customer 2"));
    }

    @Test
    void findById() throws Exception {
        var customer = Customer.builder().name("customer 1").email("c1@gmail.com")
                .salary(1000d).build();
        customerRepository.save(customer); // obtiene un id de base datos

        mockMvc.perform(
                        get("/customers/" + customer.getId())
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("customer 1"))
                .andExpect(jsonPath("$.salary").value("1000.0"));
    }

    @Test
    void findById_NotFound() throws Exception {

        mockMvc.perform(
                        get("/customers/{id}", 9999)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()) ;// 404
    }

    @Test
    void create() throws Exception {
        var customer = Customer.builder().name("customer 1").email("c1@gmail.com")
                .salary(1000d).build();

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customer))
        ).andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("customer 1"));
    }

    @Test
    void update() throws Exception {
        var customer = Customer.builder().name("customer 1").email("c1@gmail.com")
                .salary(1000d).build();
        customerRepository.save(customer);

        // simular modificar el objeto
        customer.setName(customer.getName() + " modificado");
        customer.setSalary(4000d);

        mockMvc.perform(put("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer))
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("customer 1 modificado"));

        var customerDB = customerRepository.findById(customer.getId()).orElseThrow();
        assertEquals("customer 1 modificado", customerDB.getName());
    }

    @Test
    void deleteById() throws Exception {
        var customer = Customer.builder().name("customer 1").email("c1@gmail.com")
                .salary(1000d).build();
        customerRepository.save(customer);

        mockMvc.perform(delete("/customers/{id}", customer.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent());

        var exists = customerRepository.existsById(customer.getId());
        assertFalse(exists);
    }


}