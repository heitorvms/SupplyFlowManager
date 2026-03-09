package com.projedata.SupplyFlow.Manager.repository;

import com.projedata.SupplyFlow.Manager.entity.ProductComposition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductCompositionRepository extends JpaRepository<ProductComposition, Long> {
    List<ProductComposition> findByProductCode(Long productCode);
}
