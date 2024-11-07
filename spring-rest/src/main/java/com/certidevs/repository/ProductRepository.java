package com.certidevs.repository;

import com.certidevs.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByPriceIsGreaterThanEqual(Double price);

    List<Product> findByActiveTrueAndPriceBetween(Double priceStart, Double priceEnd);

    List<Product> findByActiveTrueOrderByPriceDesc();

    List<Product> findAllByManufacturer_Id(Long id);

    List<Product> findAllByManufacturer_Name(String name);
    List<Product> findAllByManufacturer_City(String city);
    List<Product> findByManufacturer_Year(Integer year);

    // CUIDADO hay que asegurarse de que el nombre sea un atributo unico
    // Ejemplo valores unicos: email, nif, sku, matricula, isbn, iban
    Optional<Product> findByName(String name);


    // Docs oficial de JPQL: https://jakarta.ee/specifications/persistence/3.2/jakarta-persistence-spec-3.2#a4665
    @Query("""
    select p from Product p
    join fetch p.manufacturer
    where p.manufacturer.id = ?1
    """)
    List<Product> findAllByManufacturerIdEager(Long id);



}