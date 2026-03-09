package com.projedata.SupplyFlow.Manager.controller;

import com.projedata.SupplyFlow.Manager.dto.ProductCompositionRequestDTO;
import com.projedata.SupplyFlow.Manager.entity.ProductComposition;
import com.projedata.SupplyFlow.Manager.service.ProductCompositionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/product-compositions")
@CrossOrigin("http://localhost:3000/")
public class ProductCompositionController {

    private final ProductCompositionService service;

    public ProductCompositionController(ProductCompositionService service) {
        this.service = service;
    }

    @PostMapping
    public ProductComposition create(@RequestBody ProductCompositionRequestDTO dto) {

        return service.create(
                dto.getProductId(),
                dto.getRawMaterialId(),
                dto.getQuantityRequired()
        );
    }

    @GetMapping("/product/{productId}")
    public List<ProductComposition> getByProduct(@PathVariable Long productId) {
        return service.getByProduct(productId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
