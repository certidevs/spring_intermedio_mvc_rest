package com.certidevs.repository;

import com.certidevs.dto.ManufacturerWithProductStats;
import com.certidevs.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

    // https://docs.spring.io/spring-data/jpa/reference/jpa/query-methods.html#jpa.query-methods.query-creation
    List<Manufacturer> findByCityIgnoreCase(String city);

    // Metodo JPQL que me traiga los fabricantes con calculos: num productos, suma precios
    @Query("""
    select new com.certidevs.dto.ManufacturerWithProductStats(
    m.id,
    m.name,
    COUNT(p),
    SUM(p.price)
    ) from Manufacturer m
    LEFT JOIN Product p ON m.id = p.manufacturer.id
    GROUP BY m.id, m.name
    """)
    List<ManufacturerWithProductStats> findAllWithCalculatedProductStats_Unidirectional();

    @Query("""
    select new com.certidevs.dto.ManufacturerWithProductStats(
    m.id,
    m.name,
    COUNT(p),
    SUM(p.price)
    ) from Manufacturer m
    LEFT JOIN m.products p
    GROUP BY m.id, m.name
    """)
    List<ManufacturerWithProductStats> findAllWithCalculatedProductStats_Bidirectional();

    @Query("""
    select m from Manufacturer m
    join fetch m.products
    where m.id = ?1
    """)
    Optional<Manufacturer> findByIdEager(Long id);
}