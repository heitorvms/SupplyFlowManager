package com.projedata.SupplyFlow.Manager.controller;

import com.projedata.SupplyFlow.Manager.dto.ProductRequestDTO;
import com.projedata.SupplyFlow.Manager.dto.ProductResponseDTO;
import com.projedata.SupplyFlow.Manager.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000/")
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ProductResponseDTO create(
            @Validated @RequestBody ProductResponseDTO dto
    ) {
        return productService.create(dto);
    }

    @GetMapping
    public Page<ProductResponseDTO> list(Pageable pageable) {
        return productService.list(pageable);
    }

    @PutMapping("/{code}")
    public ProductResponseDTO update(
            @PathVariable Long code,
            @Validated @RequestBody ProductRequestDTO dto
    ) {
        return productService.update(code, dto);
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> delete(@PathVariable Long code) {
        productService.delete(code);
        return ResponseEntity.noContent().build();
    }

}
