package com.projedata.SupplyFlow.Manager.dto;

import lombok.Data;

@Data
public class ProductCompositionRequestDTO {

    private Long productId;
    private Long rawMaterialId;
    private Double quantityRequired;

}
