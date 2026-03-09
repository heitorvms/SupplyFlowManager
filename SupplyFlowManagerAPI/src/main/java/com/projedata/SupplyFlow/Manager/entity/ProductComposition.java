package com.projedata.SupplyFlow.Manager.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "product_composition")
public class ProductComposition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long code;

    private Double quantityRequired;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonBackReference
    private Product product;

    @ManyToOne
    @JoinColumn(name = "raw_material_id", nullable = false)
    private RawMaterial rawMaterial;

}
