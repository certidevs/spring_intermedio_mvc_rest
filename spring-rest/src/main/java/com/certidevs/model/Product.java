package com.certidevs.model;

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
    private String name;
    @Column(length = 500)
    private String description;
    private Double price;
    private Integer quantity;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean active;

}