package com.projedata.SupplyFlow.Manager.entity;

import com.projedata.SupplyFlow.Manager.enuns.UnitOfMeasure;
import jakarta.persistence.*;

@Entity
@Table(name = "quantity_stock")
public class QuantityInStock {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private Double quantity;

    @Enumerated(EnumType.STRING)
    private UnitOfMeasure unitOfMeasure;

}
