package com.example.nvt.service;

import com.example.nvt.DTO.HouseholdDetailsDTO;
import com.example.nvt.DTO.UserSummaryDTO;
import com.example.nvt.DTO.VacantApartmentDTO;
import com.example.nvt.enumeration.RealEstateType;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.exceptions.NotFoundException;
import com.example.nvt.model.*;
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

    public Household getHouseholdByIdAndRealestateId(Long realestateId, Long householdId){
        var householdWrapper = householdRepository.getHouseholdByIdAndRealestateId(realestateId, householdId);
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

    public HouseholdDetailsDTO getHouseholdDetails(Long realestateId, Long householdId) {
        Realestate realestate = realestateService.getRealestateById(realestateId);
        Household household = getHouseholdByIdAndRealestateId(realestateId, householdId);
        Client client = household.getHouseholdOwner();
        City city = realestate.getCity();
        Municipality municipality = city.getMunicipality();
        Region region = municipality.getRegion();

        List<String> imagePaths;
        if(realestate.getImages().isEmpty()) {
            imagePaths = List.of("/realestate/default/image/realestate1.png", "/realestate/default/image/realestate2.png", "/realestate/default/image/realestate3.png", "/realestate/default/image/realestate4.png");
        }else{
            imagePaths = realestate.getImages();
        }


        HouseholdDetailsDTO householdDTO = HouseholdDetailsDTO.builder()
                .realestateType(realestate.getType().toString())
                .addressStreet(realestate.getAddressStreet())
                .addressNum(realestate.getAddressNum())
                .city(city.getName())
                .municipality(municipality.getName())
                .region(region.getName())
                .totalFloors(realestate.getType() != RealEstateType.BUILDING ? realestate.getTotalFloors() : 1L)
                .apartmentNum(realestate.getType() != RealEstateType.BUILDING ? null : household.getApartmentNum())
                .images(imagePaths)
                .lat(realestate.getLat())
                .lon(realestate.getLon())
                .size(household.getSize())
                .build();

        if(client != null){
            UserSummaryDTO summaryData = UserSummaryDTO.builder()
                    .name(client.getFirstName())
                    .lastname(client.getLastname())
                    .email(client.getEmail())
                    .build();
            householdDTO.setUser(summaryData);
        }


//        UserSummaryDTO summaryData = UserSummaryDTO.builder()
//        .id(200L)
//        .name("Neki proba klijent")
//        .lastname("Neki prezimenijanovic")
//        .email("nekimejl@gmail.com")
////        .profileImg("/realestate/default/image/realestate1.png")
//        .build();
//
//        householdDTO.setUser(summaryData);

        return householdDTO;
    }
}
