package com.projedata.SupplyFlow.Manager.controller;

import com.projedata.SupplyFlow.Manager.enuns.UnitOfMeasure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class EnumControllerTest {

    @Test
    void shouldReturnAllUnitOfMeasureValues() {
        EnumController controller = new EnumController();

        List<UnitOfMeasure> result = controller.unitOfMeasureEnum();

        Assertions.assertEquals(UnitOfMeasure.values().length, result.size());
        Assertions.assertTrue(result.contains(UnitOfMeasure.UNIT));
    }
}
