package com.projedata.SupplyFlow.Manager.controller;

import com.projedata.SupplyFlow.Manager.dto.RawMaterialRequestDTO;
import com.projedata.SupplyFlow.Manager.dto.RawMaterialResponseDTO;
import com.projedata.SupplyFlow.Manager.enuns.UnitOfMeasure;
import com.projedata.SupplyFlow.Manager.service.RawMaterialService;
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

import java.util.List;

@ExtendWith(MockitoExtension.class)
class RawMaterialControllerTest {

    @Mock
    private RawMaterialService rawMaterialService;

    @InjectMocks
    private RawMaterialController rawMaterialController;

    @Test
    void shouldCreateRawMaterial() {
        RawMaterialRequestDTO request = new RawMaterialRequestDTO();
        request.setName("Steel");
        request.setQuantity(100.0);
        request.setUnitOfMeasure(UnitOfMeasure.KILOGRAM);

        RawMaterialResponseDTO expected = new RawMaterialResponseDTO(1L, "Steel", 100.0, UnitOfMeasure.KILOGRAM);
        Mockito.when(rawMaterialService.create(request)).thenReturn(expected);

        RawMaterialResponseDTO result = rawMaterialController.create(request);

        Assertions.assertEquals(1L, result.getCode());
        Assertions.assertEquals("Steel", result.getName());
        Mockito.verify(rawMaterialService).create(request);
    }

    @Test
    void shouldListRawMaterials() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<RawMaterialResponseDTO> expectedPage =
                new PageImpl<>(List.of(new RawMaterialResponseDTO(1L, "Steel", 100.0, UnitOfMeasure.KILOGRAM)));
        Mockito.when(rawMaterialService.list(pageable)).thenReturn(expectedPage);

        Page<RawMaterialResponseDTO> result = rawMaterialController.list(pageable);

        Assertions.assertEquals(1, result.getTotalElements());
        Mockito.verify(rawMaterialService).list(pageable);
    }

    @Test
    void shouldUpdateRawMaterial() {
        RawMaterialRequestDTO request = new RawMaterialRequestDTO();
        request.setName("Updated");
        request.setQuantity(50.0);
        request.setUnitOfMeasure(UnitOfMeasure.GRAM);

        RawMaterialResponseDTO expected = new RawMaterialResponseDTO(1L, "Updated", 50.0, UnitOfMeasure.GRAM);
        Mockito.when(rawMaterialService.update(1L, request)).thenReturn(expected);

        RawMaterialResponseDTO result = rawMaterialController.update(1L, request);

        Assertions.assertEquals("Updated", result.getName());
        Mockito.verify(rawMaterialService).update(1L, request);
    }

    @Test
    void shouldDeleteRawMaterial() {
        rawMaterialController.delete(1L);
        Mockito.verify(rawMaterialService).delete(1L);
    }
}
