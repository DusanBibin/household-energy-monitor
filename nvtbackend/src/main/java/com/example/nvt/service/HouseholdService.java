package com.example.nvt.service;

import com.example.nvt.model.Household;
import com.example.nvt.model.User;
import com.example.nvt.repository.HouseholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HouseholdService {
    private final HouseholdRepository householdRepository;

    public Household saveHousehold(Household household) {
        return householdRepository.save(household);
    }

}
