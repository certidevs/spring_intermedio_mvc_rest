package com.certidevs.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    // CUIDADO: es una asociación bidireccional que puede causar problemas en JSON ciclos
    // @JsonIgnore // hace que no salga la lista de productos
    @JsonIgnoreProperties({"manufacturer", "active"})
    List<Product> products = new ArrayList<>();

}