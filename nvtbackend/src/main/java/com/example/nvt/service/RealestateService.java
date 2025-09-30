package com.example.nvt.service;

import com.example.nvt.DTO.*;
import com.example.nvt.enumeration.RealEstateType;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.*;
import com.example.nvt.repository.HouseholdRepository;
import com.example.nvt.repository.RealestateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RealestateService {

    private final RealestateRepository realestateRepository;
    private final FileService fileService;

//    @Cacheable(value = "realestateImagePaths", key = "#realestateId")
    public List<RealestateImagePathsDTO> getImagePaths(List<Long> realestateIds) {

        if(realestateIds.isEmpty()) throw new InvalidInputException("The realestate IDs cannot be empty");

        List<RealestateImagePathsDTO> imagePathsDTO = new ArrayList<>();
        for(Long id : realestateIds) {
            Realestate realestate = getRealestateById(id);

            List<String> imagePaths;
            if(realestate.getImages().isEmpty()) {
                imagePaths = List.of("/realestate/default/image/realestate1.png", "/realestate/default/image/realestate2.png", "/realestate/default/image/realestate3.png", "/realestate/default/image/realestate4.png");
            }else{
                imagePaths = realestate.getImages();
            }


            RealestateImagePathsDTO r = RealestateImagePathsDTO.builder()
                    .id(realestate.getId())
                    .paths(imagePaths)
                    .build();

            imagePathsDTO.add(r);
        }

        return imagePathsDTO;
    }


    public Realestate getRealestateById(Long id){
        Optional<Realestate> realestateWrapper = realestateRepository.findById(id);
        if(realestateWrapper.isEmpty()) throw new InvalidInputException("Realestate with id " + id + " not found");
        return realestateWrapper.get();
    }

    public Realestate saveRealestate(Realestate realestate) {
        return realestateRepository.save(realestate);
    }

    @Cacheable(value = "realestateSingleImage", key = "#realestateId + '_' + #fileName")
    public String getRealestateImage(Long realestateId, String fileName) {

        Realestate realestate =  getRealestateById(realestateId);

        if(!realestate.getImages().contains(fileName)){


            if(List.of("realestate1.png", "realestate2.png", "realestate3.png", "realestate4.png").contains(fileName)){
                return "/" + fileService.uploadDirRealestates + "/default/" + fileName;
            }

            throw new InvalidInputException("This file doesn't exist");
        }

        return "/" + fileService.uploadDirRealestates + "/" + realestate.getId() + "/" + fileName;

    }

//    @Cacheable(
//            value = "realestateSummaries",
//            key = "#userId + '_' + #page + '_' + #size"
//    )
    public PagedResponse<RealestateSummaryDTO> getRealestateSummaries(Long userId, int page, int size) {

        if (page < 0) page = 0;
        if (size < 1) size = 10;

        Page<Realestate> realestates = realestateRepository.getRealestatesByClientId(
                userId,
                PageRequest.of(page, size)
        );

        List<RealestateSummaryDTO> summaries = realestates.getContent().stream().map(realestate -> {
            City c = realestate.getCity();
            Municipality m = c.getMunicipality();
            Region r = m.getRegion();

            String address = realestate.getAddressStreet() + " " + realestate.getAddressNum() + " " +
                    c.getZipCode() + " " + c.getName() + " " + m.getName() + " " + r.getName();

            // Force materialization of households into DTO-safe list
            List<HouseholdSummaryDTO> householdSummaries = realestate.getHouseholds().stream()
                    .filter(h -> h.getHouseholdOwner() != null
                            && h.getHouseholdOwner().getId().equals(userId))
                    .map(h -> HouseholdSummaryDTO.builder()
                            .id(h.getId())
                            .apartmentNumber(
                                    realestate.getType() == RealEstateType.BUILDING
                                            ? String.valueOf(h.getApartmentNum())
                                            : null
                            )
                            .build())
                    .toList();


            List<String> images = realestate.getImages() != null
                    ? List.copyOf(realestate.getImages())
                    : List.of();

            return RealestateSummaryDTO.builder()
                    .realestateId(realestate.getId())
                    .type(realestate.getType())
                    .householdSummaries(householdSummaries)
                    .address(address)
                    .images(images)
                    .build();
        }).toList();

        return PagedResponse.<RealestateSummaryDTO>builder()
                .content(summaries)
                .page(realestates.getNumber())
                .size(realestates.getSize())
                .totalElements(realestates.getTotalElements())
                .totalPages(realestates.getTotalPages())
                .build();
    }







//    public List<String> getVacantRealestateHouseholdIds(Long realestateId) {
//        Realestate realestate = getRealestateById(realestateId);
//        return householdRepository.findVacantRealestateHouseholdIds(realestateId).stream().map(Object::toString).collect(Collectors.toList());
//    }
}
