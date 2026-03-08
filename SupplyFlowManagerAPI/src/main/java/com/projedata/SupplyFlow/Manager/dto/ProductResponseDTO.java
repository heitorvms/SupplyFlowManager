package com.projedata.SupplyFlow.Manager.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {

    private Long code;
    private String name;
    private BigDecimal price;

    public ProductResponseDTO(Long code, String name, BigDecimal price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

}
