package com.projedata.SupplyFlow.Manager.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "raw_material")
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long code;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quantity_stock_id")
    private QuantityInStock quantityInStock;

}
