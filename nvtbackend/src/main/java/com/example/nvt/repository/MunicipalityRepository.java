package com.example.nvt.repository;

import com.example.nvt.model.Municipality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {

    @Query("select m from Municipality m where m.region.name = :name")
    List<Municipality> findByRegion(String name);

    @Query("select m from Municipality m join m.cities c where m.name = :munName and c.name = :cityName")
    Optional<Municipality> findByNameAndCityName(String munName, String cityName);
}
