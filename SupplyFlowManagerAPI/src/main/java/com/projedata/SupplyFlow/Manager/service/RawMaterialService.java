package com.projedata.SupplyFlow.Manager.service;

import com.projedata.SupplyFlow.Manager.dto.RawMaterialRequestDTO;
import com.projedata.SupplyFlow.Manager.dto.RawMaterialResponseDTO;
import com.projedata.SupplyFlow.Manager.entity.QuantityInStock;
import com.projedata.SupplyFlow.Manager.entity.RawMaterial;
import com.projedata.SupplyFlow.Manager.repository.RawMaterialRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;

    public RawMaterialService(RawMaterialRepository rawMaterialRepository) {
        this.rawMaterialRepository = rawMaterialRepository;
    }

    public RawMaterialResponseDTO create(RawMaterialRequestDTO dto) {

        QuantityInStock stock = new QuantityInStock();
        stock.setQuantity(dto.getQuantity());
        stock.setUnitOfMeasure(dto.getUnitOfMeasure());

        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setName(dto.getName());
        rawMaterial.setQuantityInStock(stock);

        RawMaterial saved = rawMaterialRepository.save(rawMaterial);

        return new RawMaterialResponseDTO(
                saved.getCode(),
                saved.getName(),
                saved.getQuantityInStock().getQuantity(),
                saved.getQuantityInStock().getUnitOfMeasure()
        );
    }

    public Page<RawMaterialResponseDTO> list(Pageable pageable) {

        return rawMaterialRepository
                .findAll(pageable)
                .map(material -> new RawMaterialResponseDTO(
                        material.getCode(),
                        material.getName(),
                        material.getQuantityInStock().getQuantity(),
                        material.getQuantityInStock().getUnitOfMeasure()
                ));
    }

    public RawMaterialResponseDTO update(Long code, RawMaterialRequestDTO dto) {

        RawMaterial rawMaterial = rawMaterialRepository
                .findById(code)
                .orElseThrow();

        rawMaterial.setName(dto.getName());

        QuantityInStock stock = rawMaterial.getQuantityInStock();
        stock.setQuantity(dto.getQuantity());
        stock.setUnitOfMeasure(dto.getUnitOfMeasure());

        RawMaterial updated = rawMaterialRepository.save(rawMaterial);

        return new RawMaterialResponseDTO(
                updated.getCode(),
                updated.getName(),
                stock.getQuantity(),
                stock.getUnitOfMeasure()
        );
    }

    public void delete(Long code) {
        rawMaterialRepository.deleteById(code);
    }

}
