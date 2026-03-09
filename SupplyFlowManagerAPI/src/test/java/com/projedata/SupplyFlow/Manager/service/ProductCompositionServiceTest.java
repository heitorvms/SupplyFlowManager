package com.projedata.SupplyFlow.Manager.service;

import com.projedata.SupplyFlow.Manager.entity.Product;
import com.projedata.SupplyFlow.Manager.entity.ProductComposition;
import com.projedata.SupplyFlow.Manager.entity.RawMaterial;
import com.projedata.SupplyFlow.Manager.repository.ProductCompositionRepository;
import com.projedata.SupplyFlow.Manager.repository.ProductRepository;
import com.projedata.SupplyFlow.Manager.repository.RawMaterialRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductCompositionServiceTest {

    @Mock
    private ProductCompositionRepository compositionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private ProductCompositionService productCompositionService;

    @Test
    void shouldCreateComposition() {
        Product product = new Product();
        product.setCode(1L);
        product.setName("Bread");

        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setCode(2L);
        rawMaterial.setName("Flour");

        ProductComposition saved = new ProductComposition();
        saved.setCode(10L);
        saved.setProduct(product);
        saved.setRawMaterial(rawMaterial);
        saved.setQuantityRequired(2.0);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(rawMaterialRepository.findById(2L)).thenReturn(Optional.of(rawMaterial));
        Mockito.when(compositionRepository.save(Mockito.any(ProductComposition.class))).thenReturn(saved);

        ProductComposition result = productCompositionService.create(1L, 2L, 2.0);

        Assertions.assertEquals(10L, result.getCode());
        Assertions.assertEquals(2.0, result.getQuantityRequired());
    }

    @Test
    void shouldThrowWhenProductIsMissing() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> productCompositionService.create(1L, 2L, 1.0)
        );

        Assertions.assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void shouldThrowWhenRawMaterialIsMissing() {
        Product product = new Product();
        product.setCode(1L);

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(rawMaterialRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException ex = Assertions.assertThrows(
                RuntimeException.class,
                () -> productCompositionService.create(1L, 2L, 1.0)
        );

        Assertions.assertEquals("Raw material not found", ex.getMessage());
    }

    @Test
    void shouldGetCompositionsByProduct() {
        ProductComposition composition = new ProductComposition();
        composition.setCode(11L);

        Mockito.when(compositionRepository.findByProductCode(1L)).thenReturn(List.of(composition));

        List<ProductComposition> result = productCompositionService.getByProduct(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(11L, result.get(0).getCode());
    }

    @Test
    void shouldDeleteComposition() {
        productCompositionService.delete(1L);
        Mockito.verify(compositionRepository).deleteById(1L);
    }
}
