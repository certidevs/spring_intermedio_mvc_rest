package com.certidevs.dto;

// objetos inmutables
// Alternativa: usar MapStruct framework para mapeos entre objetos, principalmente DTO y entidades:
// https://mapstruct.org/documentation/stable/reference/html/#introduction
public record ManufacturerWithProductStats(
        Long manufacturerId,
        String manufacturerName,
        Long productsCount,
        Double productsPriceSum
        ) {
}
