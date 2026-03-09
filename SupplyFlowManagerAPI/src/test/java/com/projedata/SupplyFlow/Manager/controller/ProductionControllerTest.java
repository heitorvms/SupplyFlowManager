package com.projedata.SupplyFlow.Manager.controller;

import com.projedata.SupplyFlow.Manager.dto.ProductionSuggestionDTO;
import com.projedata.SupplyFlow.Manager.service.ProductionOptimizerService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductionControllerTest {

    @Mock
    private ProductionOptimizerService productionOptimizerService;

    @InjectMocks
    private ProductionController productionController;

    @Test
    void shouldOptimizeProduction() {
        List<ProductionSuggestionDTO> expected =
                List.of(new ProductionSuggestionDTO("Bread", 5, new BigDecimal("25")));
        Mockito.when(productionOptimizerService.optimizeProduction(10)).thenReturn(expected);

        List<ProductionSuggestionDTO> result = productionController.optimize(10);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("Bread", result.get(0).getProductName());
        Mockito.verify(productionOptimizerService).optimizeProduction(10);
    }
}
