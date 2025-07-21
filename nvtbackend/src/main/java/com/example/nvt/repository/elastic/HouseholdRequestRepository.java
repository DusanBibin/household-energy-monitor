package com.example.nvt.repository.elastic;

import com.example.nvt.model.HouseholdRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface HouseholdRequestRepository extends JpaRepository<HouseholdRequest, Long> {


    @Query("select hr from HouseholdRequest hr where hr.household.id = :householdId and hr.requester.id = :clientId" +
            " and hr.requestStatus = 0")
    Optional<HouseholdRequest> getRequestByHouseholdIdAndClientId(Long householdId, Long clientId);



    @Query("select hr from HouseholdRequest hr where hr.id = :requestId and hr.household.id = :householdId")
    Optional<HouseholdRequest> getRequestByIdAndHouseholdId(Long requestId, Long householdId);


    @Query("select hr from HouseholdRequest hr where hr.household.id = :householdId")
    List<HouseholdRequest> getAllPendingHouseholdRequests(Long householdId);
}
