package com.projedata.SupplyFlow.Manager.controller;


import com.projedata.SupplyFlow.Manager.dto.ProductionSuggestionDTO;
import com.projedata.SupplyFlow.Manager.service.ProductionOptimizerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/production")
@CrossOrigin("http://localhost:3000/")
public class ProductionController {

    private final ProductionOptimizerService optimizerService;

    public ProductionController(ProductionOptimizerService optimizerService) {
        this.optimizerService = optimizerService;
    }

    @GetMapping("/optimize")
    public List<ProductionSuggestionDTO> optimize(@RequestParam(defaultValue = "10") int limit) {
        return optimizerService.optimizeProduction(limit);
    }
}
