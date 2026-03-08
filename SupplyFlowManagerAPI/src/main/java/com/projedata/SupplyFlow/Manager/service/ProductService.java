package com.projedata.SupplyFlow.Manager.service;

import com.projedata.SupplyFlow.Manager.dto.ProductRequestDTO;
import com.projedata.SupplyFlow.Manager.dto.ProductResponseDTO;
import com.projedata.SupplyFlow.Manager.entity.Product;
import com.projedata.SupplyFlow.Manager.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponseDTO create(ProductResponseDTO dto) {

        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        Product saved = productRepository.save(product);
        return new ProductResponseDTO(
                saved.getCode(),
                saved.getName(),
                saved.getPrice()
        );
    }

    public Page<ProductResponseDTO> list(Pageable pageable) {
        return productRepository
                .findAll(pageable)
                .map(product -> new ProductResponseDTO(
                        product.getCode(),
                        product.getName(),
                        product.getPrice()
                ));
    }

    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {

        Product product = productRepository
                .findById(id)
                .orElseThrow();

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());

        Product updated = productRepository.save(product);

        return new ProductResponseDTO(
                updated.getCode(),
                updated.getName(),
                updated.getPrice()
        );
    }

    public void delete(Long code) {
        Product product = productRepository
                .findById(code)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + code));

        productRepository.delete(product);
    }

}
