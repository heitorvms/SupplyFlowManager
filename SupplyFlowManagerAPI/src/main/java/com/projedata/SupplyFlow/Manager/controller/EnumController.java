package com.projedata.SupplyFlow.Manager.controller;


import com.projedata.SupplyFlow.Manager.enuns.UnitOfMeasure;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000/")
@RequestMapping("/enum")
public class EnumController {

    @GetMapping
    public List<UnitOfMeasure> unitOfMeasureEnum() {
        return List.of(UnitOfMeasure.values());
    }

}
