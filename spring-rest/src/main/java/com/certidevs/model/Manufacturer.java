package com.certidevs.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "manufacturer")
public class Manufacturer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String city;

    @Column(name = "start_year")
    // CUIDADO: year es una palabra keyword reservada en H2
    private Integer year;

    private String country;

    // Por defecto es LAZY
    @OneToMany(mappedBy = "manufacturer")
    List<Product> products = new ArrayList<>();

}