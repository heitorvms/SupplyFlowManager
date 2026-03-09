package com.projedata.SupplyFlow.Manager.service;

import com.projedata.SupplyFlow.Manager.dto.ProductionSuggestionDTO;
import com.projedata.SupplyFlow.Manager.entity.Product;
import com.projedata.SupplyFlow.Manager.entity.ProductComposition;
import com.projedata.SupplyFlow.Manager.entity.RawMaterial;
import com.projedata.SupplyFlow.Manager.repository.ProductRepository;
import com.projedata.SupplyFlow.Manager.repository.RawMaterialRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductionOptimizerService {

    private final ProductRepository productRepository;
    private final RawMaterialRepository rawMaterialRepository;

    public ProductionOptimizerService(
            ProductRepository productRepository,
            RawMaterialRepository rawMaterialRepository) {
        this.productRepository = productRepository;
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public List<ProductionSuggestionDTO> optimizeProduction(int limit) {

        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
            return Collections.emptyList();
        }


        products.sort(
            Comparator.comparingDouble(this::calculateEfficiency).reversed()
        );

        Map<Long, Double> availableStock = rawMaterialRepository.findAll()
            .stream()
            .collect(Collectors.toMap(
                RawMaterial::getCode,
                rm -> rm.getQuantityInStock().getQuantity()
            ));

        List<ProductionSuggestionDTO> result = new ArrayList<>();

        for (Product product : products) {

            int maxUnits = calculateMaxUnits(product, availableStock);

            if (maxUnits <= 0 && (product.getCompositions() == null || product.getCompositions().isEmpty())) {
                continue;
            }

            consumeStock(product, availableStock, maxUnits);

            result.add(
                new ProductionSuggestionDTO(
                    product.getName(),
                    maxUnits,
                    product.getPrice().multiply(BigDecimal.valueOf(maxUnits))
                )
            );
        }

        return result.stream().limit(limit).toList();
    }

    private int calculateMaxUnits(Product product, Map<Long, Double> stock) {

        int maxUnits = Integer.MAX_VALUE;

        for (ProductComposition comp : product.getCompositions()) {

            Long rawId = comp.getRawMaterial().getCode();

            Double available = stock.get(rawId);

            if (available == null || comp.getQuantityRequired() <= 0) {
                return 0;
            }

            int possibleUnits = (int) (available / comp.getQuantityRequired());

            maxUnits = Math.min(maxUnits, possibleUnits);
        }

        return maxUnits;
    }

    private void consumeStock(Product product, Map<Long, Double> stock, int units) {

        for (ProductComposition comp : product.getCompositions()) {

            Long rawId = comp.getRawMaterial().getCode();

            double used = comp.getQuantityRequired() * units;

            stock.put(rawId, stock.get(rawId) - used);
        }
    }

    private double calculateEfficiency(Product product) {

        if (product.getCompositions() == null || product.getCompositions().isEmpty()) {
            return 0;
        }

        double totalMaterial = product.getCompositions()
            .stream()
            .mapToDouble(ProductComposition::getQuantityRequired)
            .sum();

        if (totalMaterial == 0) {
            return 0;
        }

        return product.getPrice().doubleValue() / totalMaterial;
    }
}
