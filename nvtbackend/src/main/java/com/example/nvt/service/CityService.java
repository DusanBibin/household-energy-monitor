package com.example.nvt.service;


import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.City;
import com.example.nvt.repository.CityRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CityService {


    private CityRepository cityRepository;

    public City getCityById(Long id){
        Optional<City> cityWrapper = cityRepository.findById(id);
        if(cityWrapper.isEmpty()) throw new InvalidInputException("City not found");
        return cityWrapper.get();
    }
}
