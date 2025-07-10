package com.example.nvt.controller;


import com.example.nvt.DTO.HouseholdDetailsDTO;
import com.example.nvt.DTO.VacantApartmentDTO;
import com.example.nvt.helpers.ResponseMessage;
import com.example.nvt.model.Household;
import com.example.nvt.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping("api/v1/realestate/household/script")
    public ResponseEntity<?> getAllHouseholdIds() {

        List<Long> householdIds = householdService.getAllHouseholdIds();

        return ResponseEntity.ok(householdIds);
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping("/api/v1/realestate/{realestateId}/households/{householdId}")
    public void  getHouseholdDetails(@PathVariable Long realestateId, @PathVariable Long householdId){
        System.out.println("getHouseholdDetails");
    }


    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping("/api/v1/realestate/{realestateId}/household")
    public ResponseEntity<?>  getRealestateApartmentNumbers(@PathVariable Long realestateId){

        List<VacantApartmentDTO> households = householdService.getRealestateVacantApartments(realestateId);

        return ResponseEntity.ok(households);
    }





}
