package com.certidevs.controller;

import com.certidevs.model.Product;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

    /**
     * Metodo para traer todos los productos de base de datos y cargarlos
     * en la vista product-list.html en forma de tabla o lista o grid
     * CRUD - Retrieve o Read
     */
    @GetMapping("products")
    public String findAll(Model model) {
        // productRepository.findAll()
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

    /**
     * Metodo para recuperar un producto concreto por su id (clave primaria)
     * y cargarlo en la vista product-detail.html
     */
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

    /**
     * Metodo para cargar la vista product-form para poder acceder al formulario
     * vacío y poder crear un nuevo producto desde cero, que no exista en base de datos.
     * Cargamos un product vacío new Product() para poder rellenarlo en el formulario con inputs
     * En product-form se vinculan los inputs th:object y th:field a los atributos de este objeto
     */
    @GetMapping("products/create")
    public String getFormToCreate(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    // ACCEDER AL FORMULARIO CON DATOS RELLENOS EN CADA CAMPO PARA EDITAR UN PRODUCTO EXISTENTE, QUE YA EXISTE EN BASE DE DATOS

    /**
     * Metodo para cargar la vista product-form para poder acceder al formulario
     * con datos de un producto que ya existe en base de datos y que queremos editar / modificar / actualización
     * Se carga un producto que ya existe, para modificarle por ejemplo el precio.
     * NO se crea un nuevo producto.
     */
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

    /**
     * Recibe el envío del formulario y recibe el objeto Product con todos los datos de los input
     * del formulario.
     * Pueden pasar dos cosas:
     * 1. Que se genere un nuevo producto en base de datos con un nuevo id
     * 2. Que se modifique un producto existente en base de datos con un id que ya tenía
     */
    @PostMapping("products")
    public String save(@ModelAttribute Product product) {

        // productRepository.save(product)
        System.out.println(product);
        log.info("Received product from Form {}", product);

        return "redirect:/products";
    }

    // CRUD - Delete
    // http://localhost:8080/products/delete/1
    // http://localhost:8080/products/delete/2

    /**
     * Metodo para borrar un producto que ya existe en base de datos
     * WARNING: el producto puede tener asociaciones y su borrado puede no ser trivial y
     * requerir realizar primero desasociaciones
     */
    @GetMapping("products/delete/{id}")
    public String deleteById(@PathVariable Long id) {
        log.info("MVC Request to delete product by id: {}", id);

        // CUIDADO ya que puede haber otras entidades asociadas al producto y no se puede borrar de forma directa.

        // Opción 1 : desasociar lo que apunte a este producto o borrar directamente lo que haya asociado a este producto
        // productRepository.deleteById(id)

        // Opción 2: otra opcion simplemente desactivar el producto
        // productRepository.findById(id) + setActive(false) + save();
        return "redirect:/products";
    }


}