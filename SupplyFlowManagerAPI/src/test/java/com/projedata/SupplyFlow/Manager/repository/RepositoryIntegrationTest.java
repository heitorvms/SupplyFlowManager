package com.projedata.SupplyFlow.Manager.repository;

import com.projedata.SupplyFlow.Manager.entity.Product;
import com.projedata.SupplyFlow.Manager.entity.ProductComposition;
import com.projedata.SupplyFlow.Manager.entity.QuantityInStock;
import com.projedata.SupplyFlow.Manager.entity.RawMaterial;
import com.projedata.SupplyFlow.Manager.enuns.UnitOfMeasure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@Transactional
class RepositoryIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @Autowired
    private ProductCompositionRepository productCompositionRepository;

    @Test
    void shouldSaveAndFindProduct() {
        Product product = new Product();
        product.setName("Bread");
        product.setPrice(new BigDecimal("5.00"));

        Product saved = productRepository.save(product);

        Assertions.assertNotNull(saved.getCode());
        Assertions.assertTrue(productRepository.findById(saved.getCode()).isPresent());
    }

    @Test
    void shouldSaveAndFindRawMaterial() {
        RawMaterial material = createRawMaterial("Flour", 100.0, UnitOfMeasure.KILOGRAM);

        RawMaterial saved = rawMaterialRepository.save(material);

        Assertions.assertNotNull(saved.getCode());
        Assertions.assertTrue(rawMaterialRepository.findById(saved.getCode()).isPresent());
    }

    @Test
    void shouldFindCompositionsByProductCode() {
        Product product = new Product();
        product.setName("Cake");
        product.setPrice(new BigDecimal("12.00"));
        product = productRepository.save(product);

        RawMaterial material = rawMaterialRepository.save(
                createRawMaterial("Sugar", 20.0, UnitOfMeasure.KILOGRAM)
        );

        ProductComposition composition = new ProductComposition();
        composition.setProduct(product);
        composition.setRawMaterial(material);
        composition.setQuantityRequired(2.0);
        productCompositionRepository.save(composition);

        List<ProductComposition> result = productCompositionRepository.findByProductCode(product.getCode());

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(product.getCode(), result.get(0).getProduct().getCode());
    }

    private RawMaterial createRawMaterial(String name, Double quantity, UnitOfMeasure unit) {
        QuantityInStock stock = new QuantityInStock();
        stock.setQuantity(quantity);
        stock.setUnitOfMeasure(unit);

        RawMaterial material = new RawMaterial();
        material.setName(name);
        material.setQuantityInStock(stock);
        return material;
    }
}
