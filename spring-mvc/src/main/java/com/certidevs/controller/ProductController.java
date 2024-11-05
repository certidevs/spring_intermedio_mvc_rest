package com.certidevs.controller;

import com.certidevs.model.Product;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

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



}
