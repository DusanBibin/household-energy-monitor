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

    public HouseholdDetailsDTO getHouseholdDetails(Long householdId) {

        Household h = getHouseholdById(householdId);
        Realestate r = h.getRealestate();
        Client c = h.getHouseholdOwner();

        HouseholdDetailsDTO details = HouseholdDetailsDTO.builder()
                .imagePaths(r.getImages())
                .lat(r.getLat())
                .lon(r.getLon())
                .type(r.getType())
                .addressStreet(r.getAddressStreet())
                .addressNumber(r.getAddressNum())
                .postalCode(r.getCity().getZipCode())
                .city(r.getCity().getName())
                .municipality(r.getCity().getMunicipality().getName())
                .region(r.getCity().getMunicipality().getRegion().getName())
                .totalFloors(r.getTotalFloors())
                .size(h.getSize())
                .build();



//        if(r.getType().equals(RealEstateType.BUILDING)) {
//            details.setApartmentPerFloorNum(r.getApartmentPerFloorNum());
//            details.setApartmentNumSelector(r.get);
//        }

        if(c != null) {
            details.setOwnerName(c.getFirstName());
            details.setOwnerSurname(c.getLastname());
            details.setOwnerEmail(c.getEmail());
        }


        return details;
    }

    public Household getHouseholdById(Long id) {
        var householdWrapper = householdRepository.findById(id);
        if(householdWrapper.isEmpty()) throw new NotFoundException("Household not found");
        return householdWrapper.get();
    }

}
