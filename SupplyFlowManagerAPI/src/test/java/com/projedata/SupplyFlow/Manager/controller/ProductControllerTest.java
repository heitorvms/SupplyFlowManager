package com.projedata.SupplyFlow.Manager.controller;

import com.projedata.SupplyFlow.Manager.dto.ProductRequestDTO;
import com.projedata.SupplyFlow.Manager.dto.ProductResponseDTO;
import com.projedata.SupplyFlow.Manager.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Test
    void shouldCreateProduct() {
        ProductResponseDTO request = new ProductResponseDTO(null, "Bread", new BigDecimal("5"));
        ProductResponseDTO expected = new ProductResponseDTO(1L, "Bread", new BigDecimal("5"));
        Mockito.when(productService.create(request)).thenReturn(expected);

        ProductResponseDTO result = productController.create(request);

        Assertions.assertEquals(1L, result.getCode());
        Assertions.assertEquals("Bread", result.getName());
        Mockito.verify(productService).create(request);
    }

    @Test
    void shouldListProducts() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ProductResponseDTO> expectedPage =
                new PageImpl<>(List.of(new ProductResponseDTO(1L, "Bread", new BigDecimal("5"))));
        Mockito.when(productService.list(pageable)).thenReturn(expectedPage);

        Page<ProductResponseDTO> result = productController.list(pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(productService).list(pageable);
    }

    @Test
    void shouldUpdateProduct() {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setName("Updated");
        request.setPrice(new BigDecimal("9.90"));
        ProductResponseDTO expected = new ProductResponseDTO(1L, "Updated", new BigDecimal("9.90"));
        Mockito.when(productService.update(1L, request)).thenReturn(expected);

        ProductResponseDTO result = productController.update(1L, request);

        Assertions.assertEquals("Updated", result.getName());
        Mockito.verify(productService).update(1L, request);
    }

    @Test
    void shouldDeleteProduct() {
        ResponseEntity<Void> response = productController.delete(1L);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(productService).delete(1L);
    }
}
