package com.projedata.SupplyFlow.Manager.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long code;

    private String name;

    private BigDecimal price;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ProductComposition> compositions;

}
