package com.certidevs.dto;

// objetos inmutables

public record ManufacturerWithProductStats(
        Long manufacturerId,
        String manufacturerName,
        Long productsCount,
        Double productsPriceSum
        ) {
}
