package com.example.nvt.service;

import com.example.nvt.DTO.VacantApartmentDTO;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.exceptions.NotFoundException;
import com.example.nvt.model.Household;
import com.example.nvt.model.Realestate;
import com.example.nvt.repository.HouseholdRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseholdService {

    private final HouseholdRepository householdRepository;
    private final RealestateService realestateService;

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

    public List<VacantApartmentDTO> getRealestateVacantApartments(Long realestateId) {

        Realestate realestate = realestateService.getRealestateById(realestateId);
        if(!realestate.isVacant()) throw new InvalidInputException("Realestate is not vacant");
        List<Household> households = householdRepository.getRealestateVacantApartments(realestateId);

        return households.stream().map(h -> new VacantApartmentDTO(h.getId(),
                h.getApartmentNum() != null ? h.getApartmentNum().toString() : null
                )
        ).collect(Collectors.toList());
    }
}
