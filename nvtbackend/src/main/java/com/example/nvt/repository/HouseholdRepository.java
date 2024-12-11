package com.example.nvt.repository;

import com.example.nvt.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface HouseholdRepository extends JpaRepository<Household, Long> {


    @Query("select h.id from Household h")
    List<Long> getAllHouseholdIds();

    @Query("select h from Household h")
    List<Household> getAllHouseholds();
}
