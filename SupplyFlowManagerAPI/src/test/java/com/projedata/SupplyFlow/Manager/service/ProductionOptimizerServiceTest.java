package com.projedata.SupplyFlow.Manager.service;

import com.projedata.SupplyFlow.Manager.dto.ProductionSuggestionDTO;
import com.projedata.SupplyFlow.Manager.entity.Product;
import com.projedata.SupplyFlow.Manager.entity.ProductComposition;
import com.projedata.SupplyFlow.Manager.entity.QuantityInStock;
import com.projedata.SupplyFlow.Manager.entity.RawMaterial;
import com.projedata.SupplyFlow.Manager.repository.ProductRepository;
import com.projedata.SupplyFlow.Manager.repository.RawMaterialRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductionOptimizerServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductionOptimizerService optimizerService;

    @Test
    void shouldCalculateProductionBasedOnAvailableStock() {
        RawMaterial flour = createRawMaterial(1L, "Flour", 10.0);
        Product bread = createProduct(1L, "Bread", "5");
        bread.setCompositions(List.of(createComposition(bread, flour, 2.0)));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(bread)));

        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(flour));

        List<ProductionSuggestionDTO> result =
                optimizerService.optimizeProduction(10);

        Assertions.assertEquals(1, result.size());

        ProductionSuggestionDTO suggestion = result.get(0);

        Assertions.assertEquals("Bread", suggestion.getProductName());
        Assertions.assertEquals(5, suggestion.getQuantity());
        Assertions.assertEquals(new BigDecimal("25"), suggestion.getTotalValue());
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoProducts() {
        Mockito.when(productRepository.findAll()).thenReturn(List.of());

        List<ProductionSuggestionDTO> result = optimizerService.optimizeProduction(10);

        Assertions.assertTrue(result.isEmpty());
        Mockito.verifyNoInteractions(rawMaterialRepository);
    }

    @Test
    void shouldThrowExceptionWhenRawMaterialIsMissingFromStock() {
        RawMaterial flourInRecipe = createRawMaterial(1L, "Flour", 10.0);
        RawMaterial sugarInStock = createRawMaterial(2L, "Sugar", 10.0);
        Product cake = createProduct(1L, "Cake", "8");
        cake.setCompositions(List.of(createComposition(cake, flourInRecipe, 2.0)));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(cake)));
        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(sugarInStock));

        Assertions.assertThrows(
                NullPointerException.class,
                () -> optimizerService.optimizeProduction(10)
        );
    }

    @Test
    void shouldReturnZeroQuantityWhenRequiredAmountIsInvalid() {
        RawMaterial flour = createRawMaterial(1L, "Flour", 10.0);
        Product cake = createProduct(1L, "Cake", "8");
        cake.setCompositions(List.of(createComposition(cake, flour, 0.0)));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(cake)));
        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(flour));

        List<ProductionSuggestionDTO> result = optimizerService.optimizeProduction(10);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(0, result.get(0).getQuantity());
    }

    @Test
    void shouldProduceZeroUnitsWhenStockIsJustBelowBoundary() {
        RawMaterial flour = createRawMaterial(1L, "Flour", 1.99);
        Product bread = createProduct(1L, "Bread", "5");
        bread.setCompositions(List.of(createComposition(bread, flour, 2.0)));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(bread)));
        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(flour));

        List<ProductionSuggestionDTO> result = optimizerService.optimizeProduction(10);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(0, result.get(0).getQuantity());
    }

    @Test
    void shouldProduceOneUnitWhenStockIsExactlyAtBoundary() {
        RawMaterial flour = createRawMaterial(1L, "Flour", 2.0);
        Product bread = createProduct(1L, "Bread", "5");
        bread.setCompositions(List.of(createComposition(bread, flour, 2.0)));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(bread)));
        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(flour));

        List<ProductionSuggestionDTO> result = optimizerService.optimizeProduction(10);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.get(0).getQuantity());
        Assertions.assertEquals(new BigDecimal("5"), result.get(0).getTotalValue());
    }

    @Test
    void shouldRespectResultLimitBoundary() {
        RawMaterial flour = createRawMaterial(1L, "Flour", 10.0);
        RawMaterial sugar = createRawMaterial(2L, "Sugar", 10.0);

        Product bread = createProduct(1L, "Bread", "5");
        bread.setCompositions(List.of(createComposition(bread, flour, 2.0)));

        Product cake = createProduct(2L, "Cake", "8");
        cake.setCompositions(List.of(createComposition(cake, sugar, 2.0)));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(bread, cake)));
        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(flour, sugar));

        List<ProductionSuggestionDTO> limitZeroResult = optimizerService.optimizeProduction(0);
        List<ProductionSuggestionDTO> limitOneResult = optimizerService.optimizeProduction(1);

        Assertions.assertTrue(limitZeroResult.isEmpty());
        Assertions.assertEquals(1, limitOneResult.size());
    }

    @Test
    void shouldSkipProductWhenMaxUnitsIsZeroAndCompositionsBehaveAsEmpty() {
        RawMaterial flour = createRawMaterial(1L, "Flour", 10.0);
        Product invalid = createProduct(1L, "Invalid", "9");
        Product valid = createProduct(2L, "Valid", "5");

        ProductComposition zeroQty = createComposition(invalid, flour, 0.0);
        invalid.setCompositions(new AbstractList<>() {
            @Override
            public ProductComposition get(int index) {
                if (index == 0) {
                    return zeroQty;
                }
                throw new IndexOutOfBoundsException(index);
            }

            @Override
            public int size() {
                return 1;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }
        });
        valid.setCompositions(List.of(createComposition(valid, flour, 2.0)));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(invalid, valid)));
        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(flour));

        List<ProductionSuggestionDTO> result = optimizerService.optimizeProduction(10);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Valid", result.get(0).getProductName());
    }

    @Test
    void shouldEvaluateEfficiencyBranchWhenCompositionsIsNull() {
        RawMaterial flour = createRawMaterial(1L, "Flour", 10.0);
        Product nullCompositions = createProduct(1L, "NullComp", "9");
        Product valid = createProduct(2L, "Valid", "5");

        nullCompositions.setCompositions(null);
        valid.setCompositions(List.of(createComposition(valid, flour, 2.0)));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(nullCompositions, valid)));
        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(flour));

        Assertions.assertThrows(NullPointerException.class, () -> optimizerService.optimizeProduction(10));
    }

    @Test
    void shouldEvaluateEfficiencyBranchWhenTotalMaterialIsZero() {
        RawMaterial flour = createRawMaterial(1L, "Flour", 10.0);
        Product zeroMaterial = createProduct(1L, "ZeroMaterial", "9");
        Product valid = createProduct(2L, "Valid", "5");

        zeroMaterial.setCompositions(List.of(createComposition(zeroMaterial, flour, 0.0)));
        valid.setCompositions(List.of(createComposition(valid, flour, 2.0)));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(zeroMaterial, valid)));
        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(flour));

        List<ProductionSuggestionDTO> result = optimizerService.optimizeProduction(10);

        Assertions.assertEquals(2, result.size());
    }

    @Test
    void shouldCoverContinueBranchWhenCompositionsBecomesNullAfterMaxUnitsCalculation() {
        RawMaterial flour = createRawMaterial(1L, "Flour", 10.0);
        FlakyCompositionsProduct flaky = new FlakyCompositionsProduct();
        flaky.setCode(1L);
        flaky.setName("Flaky");
        flaky.setPrice(new BigDecimal("9"));
        flaky.firstReadCompositions = List.of(createComposition(flaky, flour, 0.0));

        Mockito.when(productRepository.findAll())
                .thenReturn(new ArrayList<>(List.of(flaky)));
        Mockito.when(rawMaterialRepository.findAll())
                .thenReturn(List.of(flour));

        List<ProductionSuggestionDTO> result = optimizerService.optimizeProduction(10);

        Assertions.assertTrue(result.isEmpty());
    }

    private static class FlakyCompositionsProduct extends Product {
        private List<ProductComposition> firstReadCompositions;
        private int reads;

        @Override
        public List<ProductComposition> getCompositions() {
            reads++;
            return reads == 1 ? firstReadCompositions : null;
        }
    }

    private RawMaterial createRawMaterial(Long code, String name, double quantity) {
        QuantityInStock stock = new QuantityInStock();
        stock.setQuantity(quantity);

        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setCode(code);
        rawMaterial.setName(name);
        rawMaterial.setQuantityInStock(stock);
        return rawMaterial;
    }

    private Product createProduct(Long code, String name, String price) {
        Product product = new Product();
        product.setCode(code);
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        return product;
    }

    private ProductComposition createComposition(Product product, RawMaterial rawMaterial, double quantityRequired) {
        ProductComposition composition = new ProductComposition();
        composition.setProduct(product);
        composition.setRawMaterial(rawMaterial);
        composition.setQuantityRequired(quantityRequired);
        return composition;
    }
}
