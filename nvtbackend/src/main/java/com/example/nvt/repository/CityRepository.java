package com.example.nvt.repository;

import com.example.nvt.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {


    Optional<City> findByName(String name);
}
