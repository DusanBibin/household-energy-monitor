package com.example.nvt.repository.elastic;

import com.example.nvt.model.HouseholdRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface HouseholdRequestRepository extends JpaRepository<HouseholdRequest, Long> {


    @Query("select hr from HouseholdRequest hr where hr.household.id = :householdId and hr.requester.id = :clientId" +
            " and hr.requestStatus = 0")
    Optional<HouseholdRequest> getRequestByClient(Long householdId, Long clientId);
}
