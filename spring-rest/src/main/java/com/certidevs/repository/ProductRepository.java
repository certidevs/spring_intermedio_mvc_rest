package com.certidevs.repository;

import com.certidevs.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByPriceIsGreaterThanEqual(Double price);

    List<Product> findByActiveTrueAndPriceBetween(Double priceStart, Double priceEnd);


}