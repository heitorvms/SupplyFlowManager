package com.projedata.SupplyFlow.Manager.entity;

import jakarta.persistence.*;

@Entity
public class ProductComposition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long code;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

    private Double quantityRequired;

}
