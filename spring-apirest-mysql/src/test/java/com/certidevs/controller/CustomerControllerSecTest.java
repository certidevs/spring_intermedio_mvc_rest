package com.certidevs.controller;

import com.certidevs.model.Customer;
import com.certidevs.model.User;
import com.certidevs.model.Role;
import com.certidevs.repository.CustomerRepository;
import com.certidevs.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class CustomerControllerSecTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private String adminToken;


    @BeforeEach
    public void setup() throws Exception {
        // Limpiar los repositorios
        customerRepository.deleteAll();
        userRepository.deleteAll();

        // Crear usuarios
        User adminUser = User.builder()
                .email("admin@example.com")
                .password(passwordEncoder.encode("admin123"))
                .role(Role.ADMIN)
                .build();

        User normalUser = User.builder()
                .email("user@example.com")
                .password(passwordEncoder.encode("user123"))
                .role(Role.USER)
                .build();

        userRepository.saveAll(Arrays.asList(adminUser, normalUser));

        // Obtener tokens JWT para los usuarios
        adminToken = authenticateAndGetToken("admin@example.com", "admin123");
        userToken = authenticateAndGetToken("user@example.com", "user123");

        // Crear clientes de prueba
        Customer customer1 = Customer.builder()
                .name("John Doe")
                .email("john@example.com")
                .salary(50000.0)
                .active(true)
                .build();

        Customer customer2 = Customer.builder()
                .name("Jane Smith")
                .email("jane@example.com")
                .salary(60000.0)
                .active(true)
                .build();

        customerRepository.saveAll(Arrays.asList(customer1, customer2));
    }

    private String authenticateAndGetToken(String email, String password) throws Exception {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("email", email);
        loginRequest.put("password", password);

        String loginRequestJson = objectMapper.writeValueAsString(loginRequest);

        var result = mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequestJson))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        Map<String, String> responseMap = objectMapper.readValue(responseContent, Map.class);
        return responseMap.get("token");
    }

    // Pruebas para usuario anónimo
    @Test
    @DisplayName("Obtener customers siendo usuario anónimo sin token - 401 no autorizado")
    public void testGetCustomersAsAnonymous() throws Exception {
        mockMvc.perform(get("/customers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Obtener customers siendo usuario con rol USER - 403 Forbidden porque nuestro rol no es suficiente")
    public void testFindActiveCustomersAsUser() throws Exception {
        mockMvc.perform(get("/customers/active1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testFindActiveCustomersAsAnonymous() throws Exception {
        mockMvc.perform(get("/customers/active1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testFindActiveCustomersAsAdmin() throws Exception {
        mockMvc.perform(get("/customers/active1")
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }

    @Test
    public void testFindActive2CustomersAsUser() throws Exception {
        mockMvc.perform(get("/customers/active2")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testFindActive2CustomersAsAnonymous() throws Exception {
        mockMvc.perform(get("/customers/active2"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testFindActive2CustomersAsAdmin() throws Exception {
        mockMvc.perform(get("/customers/active2")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());
    }


    @Test
    public void testGetCustomerByIdAsAnonymous() throws Exception {
        Customer customer = customerRepository.findAll().get(0);
        mockMvc.perform(get("/customers/{id}", customer.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCreateCustomerAsAnonymous() throws Exception {
        Customer newCustomer = Customer.builder()
                .name("New Customer")
                .email("new@example.com")
                .salary(70000.0)
                .active(true)
                .build();

        mockMvc.perform(post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isUnauthorized());
    }

    // Pruebas para usuario autenticado con rol USER
    @Test
    public void testGetCustomersAsUser() throws Exception {

        mockMvc.perform(get("/customers")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetCustomerByIdAsUser() throws Exception {
        Customer customer = customerRepository.findAll().get(0);
        mockMvc.perform(get("/customers/{id}", customer.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(customer.getName())));
    }

    @Test
    public void testCreateCustomerAsUser() throws Exception {
        Customer newCustomer = Customer.builder()
                .name("New Customer")
                .email("new@example.com")
                .salary(70000.0)
                .active(true)
                .build();

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testUpdateCustomerAsUser() throws Exception {
        Customer existingCustomer = customerRepository.findAll().get(0);
        existingCustomer.setName("Updated Name");

        mockMvc.perform(put("/customers")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingCustomer)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Usuario rol USER intenta borrar customer - No puede 403 Forbidden")
    public void testDeleteCustomerAsUser() throws Exception {
        Customer existingCustomer = customerRepository.findAll().get(0);

        mockMvc.perform(delete("/customers/{id}", existingCustomer.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    // Pruebas para usuario autenticado con rol ADMIN
    @Test
    public void testGetCustomersAsAdmin() throws Exception {
        mockMvc.perform(get("/customers")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetCustomerByIdAsAdmin() throws Exception {
        Customer customer = customerRepository.findAll().get(0);
        mockMvc.perform(get("/customers/{id}", customer.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(customer.getName())));
    }

    @Test
    public void testCreateCustomerAsAdmin() throws Exception {
        Customer newCustomer = Customer.builder()
                .name("New Customer")
                .email("new@example.com")
                .salary(70000.0)
                .active(true)
                .build();

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCustomer)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("New Customer")));
    }

    @Test
    public void testUpdateCustomerAsAdmin() throws Exception {
        Customer existingCustomer = customerRepository.findAll().get(0);
        existingCustomer.setName("Updated Name");

        mockMvc.perform(put("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingCustomer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")));
    }

    @Test
    public void testDeleteCustomerAsAdmin() throws Exception {
        Customer existingCustomer = customerRepository.findAll().get(0);

        mockMvc.perform(delete("/customers/{id}", existingCustomer.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateCustomerAsUserOtherProfile() throws Exception {

        // Crear otro cliente
        Customer customer = Customer.builder()
                .name("Jane Smith")
                .email("jane@example.com")
                .salary(60000.0)
                .active(true)
                .build();
        customerRepository.save(customer);

        // Intentar actualizar el cliente
        customer.setName("Jane Updated");

        mockMvc.perform(put("/customers/{id}", customer.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customer)))
                .andExpect(status().isForbidden());
    }

    // Test para el método con @PostAuthorize
    @Test
    public void testGetCustomerByIdAsUserActiveCustomer() throws Exception {
        // Crear un cliente activo
        Customer customer = Customer.builder()
                .name("Active Customer")
                .email("active@example.com")
                .salary(70000.0)
                .active(true)
                .build();
        customerRepository.save(customer);

        // Autenticar como usuario con rol USER
        String userToken = authenticateAndGetToken("user@example.com", "user123");

        mockMvc.perform(get("/customers/post-authorize/{id}", customer.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Active Customer")));
    }

    @Test
    public void testGetCustomerByIdAsUserInactiveCustomer() throws Exception {
        // Crear un cliente inactivo
        Customer customer = Customer.builder()
                .name("Inactive Customer")
                .email("inactive@example.com")
                .salary(80000.0)
                .active(false)
                .build();
        customerRepository.save(customer);

        // Autenticar como usuario con rol USER
        String userToken = authenticateAndGetToken("user@example.com", "user123");

        mockMvc.perform(get("/customers/post-authorize/{id}", customer.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

}
