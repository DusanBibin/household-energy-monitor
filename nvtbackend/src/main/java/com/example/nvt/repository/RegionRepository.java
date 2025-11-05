package com.example.nvt.repository;

import com.example.nvt.model.Municipality;
import com.example.nvt.model.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface RegionRepository extends JpaRepository<Region, Long> {


}
