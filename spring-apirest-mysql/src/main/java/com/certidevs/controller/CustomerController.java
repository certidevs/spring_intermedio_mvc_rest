package com.certidevs.controller;

import com.certidevs.model.Customer;
import com.certidevs.repository.CustomerRepository;
import jakarta.annotation.security.RolesAllowed;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class CustomerController {


    private CustomerRepository customerRepository;


    @GetMapping("customers")
    public ResponseEntity<List<Customer>> findAll() {
        return ResponseEntity.ok(customerRepository.findAll());
    }

    @Secured("ROLE_ADMIN")
    @GetMapping("customers/active1")
    public ResponseEntity<List<Customer>> findActiveCustomers1() {
        List<Customer> activeCustomers = customerRepository.findByActiveTrue();
        return ResponseEntity.ok(activeCustomers);
    }

    @RolesAllowed({"ADMIN"})
    @GetMapping("customers/active2")
    public ResponseEntity<List<Customer>> findActiveCustomers2() {
        List<Customer> activeCustomers = customerRepository.findByActiveTrue();
        return ResponseEntity.ok(activeCustomers);
    }

    /**
     * Permite que los usuarios con rol ADMIN vean cualquier cliente.
     * Permite que los usuarios con rol USER vean el cliente solo si está activo (active == true).
     */
    @PostAuthorize("hasRole('ADMIN') or (hasRole('USER') and returnObject.body.active == true)")
    @GetMapping("/customers/post-authorize/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return ResponseEntity.ok(customer);
    }

//

//    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")

//    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == authentication.principal.id)")
//    @GetMapping("/customers/active")
//    public ResponseEntity<List<Customer>> findActiveCustomers(@RequestParam Long userId) {
//        // Implementación para filtrar por userId si es necesario
//        List<Customer> activeCustomers = customerRepository.findByActiveTrue();
//        return ResponseEntity.ok(activeCustomers);
//    }

//    @PostAuthorize("returnObject.email == authentication.name")
//    @GetMapping("/customers/{id}")
//    public ResponseEntity<Customer> findCustomerByIdSecured(@PathVariable Long id) {
//        Customer customer = customerRepository.findById(id)
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
//        return ResponseEntity.ok(customer);
//    }

    @GetMapping("customers/{id}")
    public ResponseEntity<Customer> findById(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(customer -> {
                    log.debug("Customer found with id {}", customer.getId());
                    return ResponseEntity.ok(customer);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
    @PostMapping("customers")
    public ResponseEntity<Customer> create(@RequestBody Customer customer) {
        if(customer.getId() != null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        customerRepository.save(customer); // obtiene un id

        return ResponseEntity.status(HttpStatus.CREATED).body(customer);

    }

    @PutMapping("customers")
    public ResponseEntity<Customer> update(@RequestBody Customer customer) {
        if(customer.getId() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        customerRepository.save(customer); // actualiza el objeto que ya existe en db

        return ResponseEntity.ok(customer);

    }

    /**
    Permite que los usuarios con rol ADMIN actualicen cualquier cliente.
    Permite que los usuarios con rol USER actualicen su propio perfil,
    siempre que el email del cliente (#customer.email) coincida con el
    email del usuario autenticado (authentication.name).
     */
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #customer.email == authentication.name)")
    @PutMapping("/customers/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Actualizar los campos permitidos
        existingCustomer.setName(customer.getName());
        existingCustomer.setSalary(customer.getSalary());
        existingCustomer.setActive(customer.getActive());

        customerRepository.save(existingCustomer);

        return ResponseEntity.ok(existingCustomer);
    }

    @DeleteMapping("customers/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        try {
            customerRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

}
