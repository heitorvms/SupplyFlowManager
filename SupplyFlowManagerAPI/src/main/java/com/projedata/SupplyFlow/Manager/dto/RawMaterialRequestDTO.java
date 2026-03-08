package com.projedata.SupplyFlow.Manager.dto;

import com.projedata.SupplyFlow.Manager.enuns.UnitOfMeasure;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RawMaterialRequestDTO {

    private String name;
    private Double quantity;
    private UnitOfMeasure unitOfMeasure;

}
