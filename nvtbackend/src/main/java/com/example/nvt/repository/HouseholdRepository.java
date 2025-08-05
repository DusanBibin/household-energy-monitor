package com.example.nvt.repository;

import com.example.nvt.model.Household;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HouseholdRepository extends JpaRepository<Household, Long> {


    @Query("select h.id from Household h where h.householdOwner is not null")
    List<Long> getHouseholdIds(Pageable pageable);

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

    @Query("SELECT COUNT(h) FROM Household h WHERE h.realestate.id = :realestateId AND h.householdOwner IS NULL")
    long countUnownedHouseholds(Long realestateId);


    @Query("select h.id from Household h where h.householdOwner is not null")
    List<Long> getAllOwnedHouseholds(Pageable pageable);

    @Query("select h from Household h where h.id = :householdId and h.householdOwner.id = :clientId")
    Optional<Household> findByHouseholdIdAndClientId(Long clientId, Long householdId);
}
