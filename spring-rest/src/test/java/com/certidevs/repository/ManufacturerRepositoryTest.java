package com.certidevs.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ManufacturerRepositoryTest {

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Test
    @Sql("data_product_manufacturer.sql")
    void findAllWithCalculatedProductStats_Unidirectional() {
        // org.springframework.dao.InvalidDataAccessApiUsageException: org.hibernate.query.SemanticException: Entity join did not specify a join condition [SqmEntityJoin(com.certidevs.model.Product(p))] (specify a join condition with 'on' or use 'cross join')
        // org.springframework.dao.InvalidDataAccessResourceUsageException: JDBC exception executing SQL [select m1_0.id,m1_0.name,count(p1_0.id),sum(p1_0.price) from manufacturer m1_0 left join product p1_0 on m1_0.id=p1_0.manufacturer_id] [La columna "M1_0.ID" debe estar incluida en la lista de GROUP BY
        var manufacturerDtos = manufacturerRepository.findAllWithCalculatedProductStats_Unidirectional();
        assertEquals(2, manufacturerDtos.size());

        // consulta lanzada:
        // Hibernate: select m1_0.id,m1_0.name,count(p1_0.id),sum(p1_0.price) from manufacturer m1_0 left join product p1_0 on m1_0.id=p1_0.manufacturer_id group by m1_0.id,m1_0.name

    }

    @Test
    @Sql("data_product_manufacturer.sql")
    void findAllWithCalculatedProductStats_Bidirectional() {
        var manufacturerDtos = manufacturerRepository.findAllWithCalculatedProductStats_Bidirectional();
        assertEquals(2, manufacturerDtos.size());
    }

    @Test
    @Sql("data_product_manufacturer.sql")
    void findByIdEager() {

        // UNA ÚNICA CONSULTA:
        // Hibernate: select m1_0.id,m1_0.city,m1_0.country,m1_0.name,p1_0.manufacturer_id,p1_0.id,p1_0.active,p1_0.description,p1_0.name,p1_0.price,p1_0.quantity,m1_0.start_year from manufacturer m1_0 join product p1_0 on m1_0.id=p1_0.manufacturer_id where m1_0.id=?
        var manufacturer = manufacturerRepository.findByIdEager(1L).get();
        assertEquals(4, manufacturer.getProducts().size());
    }
}