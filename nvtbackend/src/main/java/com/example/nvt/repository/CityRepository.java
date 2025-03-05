package com.example.nvt.repository;

import com.example.nvt.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Integer> {

    @Query("select c from City c where c.name = :cityName and c.municipality.name = :munName")
    Optional<City> findByNameAndMunName(String cityName, String munName);

    Optional<City> findById(Long dbId);
}
