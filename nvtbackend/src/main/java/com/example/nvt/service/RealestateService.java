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

//    public List<String> getVacantRealestateHouseholdIds(Long realestateId) {
//        Realestate realestate = getRealestateById(realestateId);
//        return householdRepository.findVacantRealestateHouseholdIds(realestateId).stream().map(Object::toString).collect(Collectors.toList());
//    }
}
