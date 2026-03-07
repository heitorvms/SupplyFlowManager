package com.projedata.SupplyFlow.Manager.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {

    private Long id;
    private String name;
    private BigDecimal price;

    public ProductResponseDTO(Long id, String name, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

}
