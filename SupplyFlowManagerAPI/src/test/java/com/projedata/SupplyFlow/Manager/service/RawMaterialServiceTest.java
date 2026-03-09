package com.projedata.SupplyFlow.Manager.service;

import com.projedata.SupplyFlow.Manager.dto.RawMaterialRequestDTO;
import com.projedata.SupplyFlow.Manager.dto.RawMaterialResponseDTO;
import com.projedata.SupplyFlow.Manager.entity.QuantityInStock;
import com.projedata.SupplyFlow.Manager.entity.RawMaterial;
import com.projedata.SupplyFlow.Manager.enuns.UnitOfMeasure;
import com.projedata.SupplyFlow.Manager.repository.RawMaterialRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class RawMaterialServiceTest {

    @Mock
    private RawMaterialRepository rawMaterialRepository;

    @InjectMocks
    private RawMaterialService rawMaterialService;

    @Test
    void shouldCreateRawMaterial() {
        RawMaterialRequestDTO dto = new RawMaterialRequestDTO();
        dto.setName("Steel");
        dto.setQuantity(100.0);
        dto.setUnitOfMeasure(UnitOfMeasure.KILOGRAM);

        RawMaterial saved = createRawMaterial(1L, "Steel", 100.0, UnitOfMeasure.KILOGRAM);

        Mockito.when(rawMaterialRepository.save(Mockito.any(RawMaterial.class))).thenReturn(saved);

        RawMaterialResponseDTO result = rawMaterialService.create(dto);

        Assertions.assertEquals(1L, result.getCode());
        Assertions.assertEquals("Steel", result.getName());
        Assertions.assertEquals(100.0, result.getQuantity());
        Assertions.assertEquals(UnitOfMeasure.KILOGRAM, result.getUnitOfMeasure());

        ArgumentCaptor<RawMaterial> captor = ArgumentCaptor.forClass(RawMaterial.class);
        Mockito.verify(rawMaterialRepository).save(captor.capture());
        Assertions.assertEquals("Steel", captor.getValue().getName());
    }

    @Test
    void shouldListRawMaterials() {
        PageRequest pageable = PageRequest.of(0, 10);
        RawMaterial material = createRawMaterial(1L, "Steel", 50.0, UnitOfMeasure.KILOGRAM);
        Page<RawMaterial> page = new PageImpl<>(List.of(material), pageable, 1);

        Mockito.when(rawMaterialRepository.findAll(pageable)).thenReturn(page);

        Page<RawMaterialResponseDTO> result = rawMaterialService.list(pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Assertions.assertEquals("Steel", result.getContent().get(0).getName());
    }

    @Test
    void shouldUpdateRawMaterial() {
        RawMaterial existing = createRawMaterial(1L, "Old Name", 10.0, UnitOfMeasure.GRAM);
        RawMaterialRequestDTO dto = new RawMaterialRequestDTO();
        dto.setName("New Name");
        dto.setQuantity(20.0);
        dto.setUnitOfMeasure(UnitOfMeasure.KILOGRAM);

        Mockito.when(rawMaterialRepository.findById(1L)).thenReturn(Optional.of(existing));
        Mockito.when(rawMaterialRepository.save(existing)).thenReturn(existing);

        RawMaterialResponseDTO result = rawMaterialService.update(1L, dto);

        Assertions.assertEquals("New Name", result.getName());
        Assertions.assertEquals(20.0, result.getQuantity());
        Assertions.assertEquals(UnitOfMeasure.KILOGRAM, result.getUnitOfMeasure());
    }

    @Test
    void shouldDeleteRawMaterial() {
        rawMaterialService.delete(1L);
        Mockito.verify(rawMaterialRepository).deleteById(1L);
    }

    private RawMaterial createRawMaterial(Long code, String name, Double quantity, UnitOfMeasure unit) {
        QuantityInStock stock = new QuantityInStock();
        stock.setQuantity(quantity);
        stock.setUnitOfMeasure(unit);

        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setCode(code);
        rawMaterial.setName(name);
        rawMaterial.setQuantityInStock(stock);
        return rawMaterial;
    }
}
