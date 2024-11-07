package com.certidevs.repository;

import com.certidevs.model.Product;
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

    @Test
    void findAllByPriceIsGreaterThanEqual() {
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
}