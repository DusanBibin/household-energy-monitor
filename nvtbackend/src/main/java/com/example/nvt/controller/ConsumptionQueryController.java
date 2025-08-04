package com.example.nvt.controller;


import com.example.nvt.DTO.ConsumptionDTO;
import com.example.nvt.model.Client;
import com.example.nvt.service.ConsumptionQueryService;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConsumptionQueryController {


    private final ConsumptionQueryService consumptionQueryService;

    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/household/{householdId}/monthly")
    public ResponseEntity<List<ConsumptionDTO>> getMonthlySum(
            @AuthenticationPrincipal Client client,
            @PathVariable Long householdId,
            @RequestParam Integer startYear,
            @RequestParam Integer startMonth) {


        List<ConsumptionDTO> consumptionDTOList = consumptionQueryService.getYearlyConsumption(client, householdId, startYear, startMonth);

        return ResponseEntity.ok(consumptionDTOList);

    }

    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/household/{householdId}/daily")
    public ResponseEntity<List<ConsumptionDTO>> getDailySum(
            @AuthenticationPrincipal Client client,
            @PathVariable Long householdId,
            @RequestParam int year,
            @RequestParam int month) {


        List<ConsumptionDTO> consumptionDTOList = consumptionQueryService.getMonthlyConsumption(client, householdId, year, month);


        return ResponseEntity.ok(consumptionDTOList);
    }

    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("household/{householdId}/consumption")
    public ResponseEntity<List<ConsumptionDTO>> getConsumption(
            @AuthenticationPrincipal Client client,
            @PathVariable Long householdId,
            @RequestParam(required = false) String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {


        List<ConsumptionDTO> consumption = consumptionQueryService.getConsumption(householdId, client, period, from, to);

        return ResponseEntity.ok(consumption);
    }


}