package com.example.nvt.service;

import com.example.nvt.DTO.RealestateImagePathsDTO;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.Realestate;
import com.example.nvt.repository.HouseholdRepository;
import com.example.nvt.repository.RealestateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RealestateService {

    private final RealestateRepository realestateRepository;

    private final HouseholdRepository householdRepository;

    public List<RealestateImagePathsDTO> getImagePaths(List<Long> realestateIds) {

        if(realestateIds.isEmpty()) throw new InvalidInputException("The realestate IDs cannot be empty");

        List<RealestateImagePathsDTO> imagePaths = new ArrayList<>();
        for(Long id : realestateIds) {
            Realestate realestate = getRealestateById(id);


//            if(realestate.getImages().isEmpty()) throw new InvalidInputException("Realestate doesn't contain any images");

            RealestateImagePathsDTO r = RealestateImagePathsDTO.builder()
                    .id(realestate.getId())
                    .paths(List.of("/realestate/1/image/realestate1.png", "/realestate/1/image/realestate2.png", "/realestate/1/image/realestate3.png", "/realestate/1/image/realestate4.png"))
                    .build();

            imagePaths.add(r);
        }

        return imagePaths;
    }


    public Realestate getRealestateById(Long id){
        Optional<Realestate> realestateWrapper = realestateRepository.findById(id);
        if(realestateWrapper.isEmpty()) throw new InvalidInputException("Realestate with id " + id + " not found");
        return realestateWrapper.get();
    }

//    public List<String> getVacantRealestateHouseholdIds(Long realestateId) {
//        Realestate realestate = getRealestateById(realestateId);
//        return householdRepository.findVacantRealestateHouseholdIds(realestateId).stream().map(Object::toString).collect(Collectors.toList());
//    }
}
