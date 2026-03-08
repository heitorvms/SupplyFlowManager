package com.projedata.SupplyFlow.Manager.controller;

import com.projedata.SupplyFlow.Manager.dto.RawMaterialRequestDTO;
import com.projedata.SupplyFlow.Manager.dto.RawMaterialResponseDTO;
import com.projedata.SupplyFlow.Manager.service.RawMaterialService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000/")
@RequestMapping("/raw-materials")
public class RawMaterialController {

    private final RawMaterialService rawMaterialService;

    public RawMaterialController(RawMaterialService rawMaterialService) {
        this.rawMaterialService = rawMaterialService;
    }

    @PostMapping
    public RawMaterialResponseDTO create(
            @Validated @RequestBody RawMaterialRequestDTO dto
    ) {
        return rawMaterialService.create(dto);
    }

    @GetMapping
    public Page<RawMaterialResponseDTO> list(Pageable pageable) {
        return rawMaterialService.list(pageable);
    }

    @PutMapping("/{code}")
    public RawMaterialResponseDTO update(
            @PathVariable Long code,
            @Validated @RequestBody RawMaterialRequestDTO dto
    ) {
        return rawMaterialService.update(code, dto);
    }

    @DeleteMapping("/{code}")
    public void delete(@PathVariable Long code) {
        rawMaterialService.delete(code);
    }

}
