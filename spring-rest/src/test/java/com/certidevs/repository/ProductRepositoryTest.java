package com.certidevs.repository;

import com.certidevs.model.Manufacturer;
import com.certidevs.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ManufacturerRepository manufacturerRepository;


    @Test
    void findAllByPriceIsGreaterThanEqual() {
        // fixture. SE puede mover a un BeforeEach o BeforeAll
        productRepository.save(
                Product.builder().name("prod1").description("prod1").active(true).quantity(1).price(11d).build()
        );
        productRepository.saveAll(List.of(
                Product.builder().name("prod2").description("prod2").active(false).quantity(1).price(23.50).build(),
                Product.builder().name("prod3").description("prod3").active(true).quantity(1).price(43.50).build(),
                Product.builder().name("prod4").description("prod4").quantity(1).price(63.50).build(),
                Product.builder().name("prod5").description("prod5").quantity(1).price(93.50).build()
        ));

        long productsCount = productRepository.count();
        assertEquals(5, productsCount);
        assertTrue(productsCount >= 5);

        productRepository.findAll().stream()
                .peek(p -> assertTrue(p.getName().startsWith("prod"))).toList();

        var products = productRepository.findAllByPriceIsGreaterThanEqual(40d);
        assertEquals(3, products.size());
        assertEquals("prod3", products.getFirst().getName());
        assertEquals("prod3", products.get(0).getName());
    }

    @Test
    @Sql("data_product.sql")
    void findByActiveTrueAndPriceBetween() {
        long productsCount = productRepository.count();
        assertEquals(8, productsCount);

        var products = productRepository.findByActiveTrueAndPriceBetween(30d, 80d);
        assertEquals(3, products.size());
        products.forEach(p -> assertTrue(p.getPrice() >= 30 && p.getPrice() <= 80 && p.getActive()));
    }

    @Test
    @Sql("data_product.sql")
    void findByActiveTrueOrderByPriceDesc() {
        var products = productRepository.findByActiveTrueOrderByPriceDesc();
        assertEquals(3, products.size());
        assertEquals(79, products.getFirst().getPrice());
        assertEquals(50, products.get(1).getPrice());
        assertEquals(32, products.getLast().getPrice());
    }


    @Test
    void findAllByManufacturer_Id() {
        var manufacturer = manufacturerRepository.save(
                Manufacturer.builder().name("Adidas").city("Madrid").year(2019).build()
        );
        // product.setManufacturer(manufacturer);
         productRepository.saveAll(List.of(
                Product.builder().name("prod2").description("prod2").active(false).quantity(1).price(23.50).build(),
                Product.builder().name("prod3").description("prod3").active(true).quantity(1).price(43.50).build(),
                Product.builder().name("prod4").description("prod4").quantity(1).price(63.50).manufacturer(manufacturer).build(),
                Product.builder().name("prod5").description("prod5").quantity(1).price(93.50).manufacturer(manufacturer).build()
        ));

        var products = productRepository.findAllByManufacturer_Id(manufacturer.getId());
        assertEquals(2, products.size());

        assertEquals(manufacturer.getName(), products.get(0).getManufacturer().getName());
        assertEquals(manufacturer.getName(), products.get(1).getManufacturer().getName());

    }

    @Test
    @DisplayName("Test para filtrar productos por fabricante, de forma lazy, es decir, sin cargar fabricante hasta que no se necesite")
    @Sql("data_product_manufacturer.sql")
    void findByManufacturer_Lazy() {

        // Hibernate: select p1_0.id,p1_0.active,p1_0.description,p1_0.manufacturer_id,p1_0.name,p1_0.price,p1_0.quantity from product p1_0 where p1_0.manufacturer_id=?
        var products = productRepository.findAllByManufacturer_Id(1L);
        assertEquals(4, products.size());

        // LANZA UNA SEGUNDA CONSULTA PORQUE manufacturer es LAZY
        // Hibernate: select m1_0.id,m1_0.city,m1_0.country,m1_0.name,m1_0.start_year from manufacturer m1_0 where m1_0.id=?
        assertEquals("Adidas", products.get(0).getManufacturer().getName());
        assertEquals("Adidas", products.get(1).getManufacturer().getName());
    }
    @Test
    @DisplayName("Test para filtrar productos por fabricante, de forma eager, es decir, trae el fabricante en la misma consulta")
    @Sql("data_product_manufacturer.sql")
    void findByManufacturer_Eager() {

        // LANZA UNA ÚNICA CONSULTA: Hibernate: select p1_0.id,p1_0.active,p1_0.description,m1_0.id,m1_0.city,m1_0.country,m1_0.name,m1_0.start_year,p1_0.name,p1_0.price,p1_0.quantity from product p1_0 join manufacturer m1_0 on m1_0.id=p1_0.manufacturer_id where p1_0.manufacturer_id=?
        var products = productRepository.findAllByManufacturerIdEager(1L);
        assertEquals(4, products.size());

        assertEquals("Adidas", products.get(0).getManufacturer().getName());
        assertEquals("Adidas", products.get(1).getManufacturer().getName());
    }

}





