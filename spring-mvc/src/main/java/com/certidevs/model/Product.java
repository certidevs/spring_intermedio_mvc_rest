package com.certidevs.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Product {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private Boolean active;
}
