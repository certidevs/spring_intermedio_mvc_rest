package com.certidevs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //@Column(unique = true)
    private String name;

    @Column(length = 500)
    private String description;
    private Double price;
    private Integer quantity;
    @Column(columnDefinition = "boolean default true")
    private Boolean active;

    // Evitar que traiga el fabricante por defecto, para optimizar consultas
    @ManyToOne(fetch = FetchType.LAZY)
    private Manufacturer manufacturer;

}