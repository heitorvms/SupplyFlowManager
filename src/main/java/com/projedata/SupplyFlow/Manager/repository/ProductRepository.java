package com.projedata.SupplyFlow.Manager.repository;

import com.projedata.SupplyFlow.Manager.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
