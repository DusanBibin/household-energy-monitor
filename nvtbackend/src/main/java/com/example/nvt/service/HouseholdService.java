package com.example.nvt.service;

import com.example.nvt.DTO.HouseholdDetailsDTO;
import com.example.nvt.enumeration.RealEstateType;
import com.example.nvt.exceptions.NotFoundException;
import com.example.nvt.model.Client;
import com.example.nvt.model.Household;
import com.example.nvt.model.Realestate;
import com.example.nvt.model.User;
import com.example.nvt.repository.HouseholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HouseholdService {

    private final HouseholdRepository householdRepository;


    public Household saveHousehold(Household household) {
        return householdRepository.save(household);
    }

    public List<Long> getAllHouseholdIds() {
        return householdRepository.getAllHouseholdIds();
    }


    public Household getHouseholdById(Long id) {
        var householdWrapper = householdRepository.findById(id);
        if(householdWrapper.isEmpty()) throw new NotFoundException("Household not found");
        return householdWrapper.get();
    }

}
