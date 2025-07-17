package com.example.nvt.repository;

import com.example.nvt.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HouseholdRepository extends JpaRepository<Household, Long> {


    @Query("select h.id from Household h")
    List<Long> getAllHouseholdIds();

    @Query("select h from Household h")
    List<Household> getAllHouseholds();

    @Query("select h.id from Household h join Realestate r on h.realestate.id = r.id" +
            " where r.id = :realestateId and h.householdOwner is null")
    List<Long> findVacantRealestateHouseholdIds(Long realestateId);

    @Query("select h from Household h join Realestate r on h.realestate.id = r.id where r.id = :realestateId " +
            "and h.householdOwner is null")
    List<Household> getRealestateVacantApartments(Long realestateId);


    @Query("select h from Household h join Realestate r on h.realestate.id = r.id and r.id = :realestateId and h.id = :householdId")
    Optional<Household> getHouseholdByIdAndRealestateId(Long realestateId, Long householdId);
}
