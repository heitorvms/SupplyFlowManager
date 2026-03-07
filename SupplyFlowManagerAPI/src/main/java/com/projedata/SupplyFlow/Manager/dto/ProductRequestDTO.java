package com.projedata.SupplyFlow.Manager.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    private String name;
    private BigDecimal price;

}
