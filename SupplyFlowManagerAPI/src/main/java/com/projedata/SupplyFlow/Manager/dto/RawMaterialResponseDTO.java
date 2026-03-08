package com.projedata.SupplyFlow.Manager.dto;

import com.projedata.SupplyFlow.Manager.enuns.UnitOfMeasure;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RawMaterialResponseDTO {

    private Long code;
    private String name;
    private Double quantity;
    private UnitOfMeasure unitOfMeasure;

    public RawMaterialResponseDTO(Long code, String name, Double quantity, UnitOfMeasure unitOfMeasure) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.unitOfMeasure = unitOfMeasure;
    }

}
