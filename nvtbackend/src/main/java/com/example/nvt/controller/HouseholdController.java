package com.example.nvt.controller;


import com.example.nvt.helpers.ResponseMessage;
import com.example.nvt.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/household")
@RequiredArgsConstructor
public class HouseholdController {

    private final HouseholdService householdService;

    @GetMapping("/script")
    public ResponseEntity<?> getAllHouseholdIds() {

        List<Long> householdIds = householdService.getAllHouseholdIds();

        return ResponseEntity.ok(householdIds);

    }

}
