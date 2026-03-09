package com.projedata.SupplyFlow.Manager.controller;

import com.projedata.SupplyFlow.Manager.dto.ProductCompositionRequestDTO;
import com.projedata.SupplyFlow.Manager.entity.Product;
import com.projedata.SupplyFlow.Manager.entity.ProductComposition;
import com.projedata.SupplyFlow.Manager.entity.RawMaterial;
import com.projedata.SupplyFlow.Manager.service.ProductCompositionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductCompositionControllerTest {

    @Mock
    private ProductCompositionService productCompositionService;

    @InjectMocks
    private ProductCompositionController productCompositionController;

    @Test
    void shouldCreateComposition() {
        ProductCompositionRequestDTO request = new ProductCompositionRequestDTO();
        request.setProductId(1L);
        request.setRawMaterialId(2L);
        request.setQuantityRequired(3.0);

        ProductComposition expected = new ProductComposition();
        expected.setCode(10L);
        expected.setProduct(new Product());
        expected.setRawMaterial(new RawMaterial());
        expected.setQuantityRequired(3.0);

        Mockito.when(productCompositionService.create(1L, 2L, 3.0)).thenReturn(expected);

        ProductComposition result = productCompositionController.create(request);

        Assertions.assertEquals(10L, result.getCode());
        Mockito.verify(productCompositionService).create(1L, 2L, 3.0);
    }

    @Test
    void shouldGetCompositionsByProduct() {
        ProductComposition composition = new ProductComposition();
        composition.setCode(11L);
        Mockito.when(productCompositionService.getByProduct(1L)).thenReturn(List.of(composition));

        List<ProductComposition> result = productCompositionController.getByProduct(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(11L, result.get(0).getCode());
        Mockito.verify(productCompositionService).getByProduct(1L);
    }

    @Test
    void shouldDeleteComposition() {
        productCompositionController.delete(1L);
        Mockito.verify(productCompositionService).delete(1L);
    }
}
