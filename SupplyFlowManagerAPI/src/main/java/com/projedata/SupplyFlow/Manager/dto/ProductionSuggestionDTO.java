package com.projedata.SupplyFlow.Manager.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductionSuggestionDTO {

    private String productName;
    private Integer quantity;
    private BigDecimal totalValue;

    public ProductionSuggestionDTO(String productName, Integer quantity, BigDecimal totalValue) {
        this.productName = productName;
        this.quantity = quantity;
        this.totalValue = totalValue;
    }

}
