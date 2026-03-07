package com.projedata.SupplyFlow.Manager.repository;

import com.projedata.SupplyFlow.Manager.dto.ProductResponseDTO;
import com.projedata.SupplyFlow.Manager.entity.Product;
import org.hibernate.query.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<ProductResponseDTO> findByProduct(Product product, Pageable pageable);
}
