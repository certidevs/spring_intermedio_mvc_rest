package com.certidevs.controller;

import com.certidevs.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Slf4j
@Controller
public class ProductController {
    // private ProductService productService;
  // private ProductRepository productRepository;

    // http://localhost:8080/products
    @GetMapping("products")
    public String findAll(Model model) {
        var products = List.of(
                new Product(1L, "microfono", "micro guay", 56.32, 2, true),
                Product.builder().name("monitor").id(2L).price(32.1).active(false).quantity(3).build(),
                Product.builder().name("ordenador MSI").id(3L).price(32.1).active(false).quantity(3).build(),
                Product.builder().name("ordenador ASUS").id(4L).price(32.1).active(false).quantity(3).build()
        );

        model.addAttribute("mensaje", "Hola mundo desde Spring MVC");
        model.addAttribute("products", products);

        return "product-list"; // vista
    }

    // http://localhost:8080/products/1
    // http://localhost:8080/products/2
    // http://localhost:8080/products/3
    @GetMapping("products/{id}")
    public String findById(@PathVariable Long id, Model model) {
        // productRepository.findById(id)
        var product = Product.builder()
                .name("monitor").id(id).price(32.1).active(false).quantity(3)
                .build();

        model.addAttribute("product", product);
        // ratingRepository.findAllByProductId() y crgar en el model
        // technicalSpecifications.findAllByProductId()

        return "product-detail";
    }

    // ACCEDER AL FORMULARIO VACÍO PARA PODER CREAR UN NUEVO PRODUCTO
    // http://localhost:8080/products/create
    @GetMapping("products/create")
    public String getFormToCreate(Model model) {
        // Metodo para acceder a la pantalla formulario product-form para CREAR NUEVO PRODUCTO
        // cargar producto vacío porque queremos rellenarlo en los input del form
        model.addAttribute("product", new Product());
        return "product-form";
    }

    // ACCEDER AL FORMULARIO CON DATOS RELLENOS EN CADA CAMPO PARA EDITAR UN PRODUCTO EXISTENTE, QUE YA EXISTE EN BASE DE DATOS
    @GetMapping("products/edit/{id}")
    public String getFormToEdit(@PathVariable Long id, Model model) {
        // productRepository.findById()
        var product = Product.builder()
                .name("monitor").id(id).price(32.1).active(false).quantity(3)
                .build();
        model.addAttribute("product", product);
        return "product-form";
    }

    // recibir el formulario enviado
    @PostMapping("products")
    public String save(@ModelAttribute Product product) {

        // productRepository.save(product)
        System.out.println(product);
        log.info("Received product from Form {}", product);

        return "redirect:/products";
    }


}