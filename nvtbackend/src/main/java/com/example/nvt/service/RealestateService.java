package com.example.nvt.service;

import com.example.nvt.model.Realestate;
import com.example.nvt.repository.RealestateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RealestateService {

    private final RealestateRepository realestateRepository;

    public Realestate saveRealestate(Realestate realestate) {
        return realestateRepository.save(realestate);
    }

}
