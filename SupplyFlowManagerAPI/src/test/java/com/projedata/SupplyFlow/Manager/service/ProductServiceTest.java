package com.projedata.SupplyFlow.Manager.service;

import com.projedata.SupplyFlow.Manager.dto.ProductRequestDTO;
import com.projedata.SupplyFlow.Manager.dto.ProductResponseDTO;
import com.projedata.SupplyFlow.Manager.entity.Product;
import com.projedata.SupplyFlow.Manager.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProduct() {
        ProductResponseDTO dto = new ProductResponseDTO(null, "Bread", new BigDecimal("5"));
        Product saved = createProduct(1L, "Bread", "5");

        Mockito.when(productRepository.save(Mockito.any(Product.class))).thenReturn(saved);

        ProductResponseDTO result = productService.create(dto);

        Assertions.assertEquals(1L, result.getCode());
        Assertions.assertEquals("Bread", result.getName());
        Assertions.assertEquals(new BigDecimal("5"), result.getPrice());
    }

    @Test
    void shouldListProducts() {
        PageRequest pageable = PageRequest.of(0, 10);
        Product product = createProduct(1L, "Cake", "12.50");
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);

        Mockito.when(productRepository.findAll(pageable)).thenReturn(page);

        Page<ProductResponseDTO> result = productService.list(pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Cake", result.getContent().get(0).getName());
    }

    @Test
    void shouldUpdateProduct() {
        Product existing = createProduct(1L, "Old", "1");
        ProductRequestDTO dto = new ProductRequestDTO();
        dto.setName("New");
        dto.setPrice(new BigDecimal("2.50"));

        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(productRepository.save(existing)).thenReturn(existing);

        ProductResponseDTO result = productService.update(1L, dto);

        Assertions.assertEquals("New", result.getName());
        Assertions.assertEquals(new BigDecimal("2.50"), result.getPrice());
    }

    @Test
    void shouldDeleteProductWhenItExists() {
        Product existing = createProduct(1L, "Item", "3");
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        productService.delete(1L);

        Mockito.verify(productRepository).delete(existing);
    }

    @Test
    void shouldThrowWhenDeletingMissingProduct() {
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> productService.delete(1L));
    }

    private Product createProduct(Long code, String name, String price) {
        Product product = new Product();
        product.setCode(code);
        product.setName(name);
        product.setPrice(new BigDecimal(price));
        return product;
    }
}
