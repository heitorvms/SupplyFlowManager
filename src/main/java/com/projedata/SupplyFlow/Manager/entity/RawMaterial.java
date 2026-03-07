package com.projedata.SupplyFlow.Manager.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "raw_material")
public class RawMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Code;

    private String Name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "quantity_stock_id")
    private QuantityInStock quantityInStock;

}
