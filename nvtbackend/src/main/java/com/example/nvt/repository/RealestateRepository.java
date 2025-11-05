package com.example.nvt.repository;

import com.example.nvt.model.Realestate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RealestateRepository extends JpaRepository<Realestate, Long> {

    @Query("select r from Realestate r")
    List<Realestate> getAllRealestates();


    @Query("select distinct r from Realestate r join Household h on h.realestate.id = r.id where h.householdOwner.id = :userId")
    Page<Realestate> getRealestatesByClientId(Long userId, PageRequest pageRequest);
}
