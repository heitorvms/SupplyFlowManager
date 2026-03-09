package com.projedata.SupplyFlow.Manager.service;

import com.projedata.SupplyFlow.Manager.entity.Product;
import com.projedata.SupplyFlow.Manager.entity.ProductComposition;
import com.projedata.SupplyFlow.Manager.entity.RawMaterial;
import com.projedata.SupplyFlow.Manager.repository.ProductCompositionRepository;
import com.projedata.SupplyFlow.Manager.repository.ProductRepository;
import com.projedata.SupplyFlow.Manager.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductCompositionService {

    private final ProductCompositionRepository compositionRepository;
    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductCompositionService(
            ProductCompositionRepository compositionRepository,
            ProductRepository productRepository,
            RawMaterialRepository rawMaterialRepository
    ) {
        this.compositionRepository = compositionRepository;
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public ProductComposition create(Long productId, Long rawMaterialId, Double quantity) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        RawMaterial material = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new RuntimeException("Raw material not found"));

        ProductComposition composition = new ProductComposition();
        composition.setProduct(product);
        composition.setRawMaterial(material);
        composition.setQuantityRequired(quantity);

        return compositionRepository.save(composition);
    }

    public List<ProductComposition> getByProduct(Long productId) {
        return compositionRepository.findByProductCode(productId);
    }

    public void delete(Long id) {
        compositionRepository.deleteById(id);
    }
}
