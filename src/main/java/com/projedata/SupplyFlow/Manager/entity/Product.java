package com.projedata.SupplyFlow.Manager.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Code;

    private String Name;

    private BigDecimal Price;

    @OneToMany(mappedBy = "product")
    private List<ProductComposition> compositions;

}
