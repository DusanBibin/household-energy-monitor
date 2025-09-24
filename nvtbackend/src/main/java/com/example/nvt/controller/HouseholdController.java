package com.example.nvt.controller;


import com.example.nvt.DTO.HouseholdDetailsDTO;
import com.example.nvt.DTO.VacantApartmentDTO;
import com.example.nvt.helpers.ResponseMessage;
import com.example.nvt.model.Household;
import com.example.nvt.model.User;
import com.example.nvt.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HouseholdController {

    private final HouseholdService householdService;

    @GetMapping("api/v1/realestate/household/script/{householdsNum}")
    public ResponseEntity<?> getAllHouseholdIds(@PathVariable int householdsNum) {

        List<Long> householdIds = householdService.getAllHouseholdIds(householdsNum);

        return ResponseEntity.ok(householdIds);
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping("/api/v1/realestate/{realestateId}/household/{householdId}")
    public ResponseEntity<?> getHouseholdDetails(@AuthenticationPrincipal User user, @PathVariable Long realestateId, @PathVariable Long householdId){
        HouseholdDetailsDTO household = householdService.getHouseholdDetails(user, realestateId, householdId);
        return ResponseEntity.ok(household);
    }


    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping("/api/v1/realestate/{realestateId}/vacant-apartment-numbers")
    public ResponseEntity<?>  getRealestateApartmentNumbers(@PathVariable Long realestateId){

        List<VacantApartmentDTO> households = householdService.getRealestateVacantApartments(realestateId);

        return ResponseEntity.ok(households);
    }








}
