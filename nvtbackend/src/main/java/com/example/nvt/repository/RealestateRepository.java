package com.example.nvt.repository;

import com.example.nvt.model.Realestate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RealestateRepository extends JpaRepository<Realestate, Long> {

    @Query("select r from Realestate r")
    List<Realestate> getAllRealestates();


}
