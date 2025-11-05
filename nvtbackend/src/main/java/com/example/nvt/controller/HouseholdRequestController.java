package com.example.nvt.controller;


import com.example.nvt.DTO.HouseholdDetailsDTO;
import com.example.nvt.DTO.HouseholdRequestDTO;
import com.example.nvt.DTO.HouseholdRequestPreviewDTO;
import com.example.nvt.enumeration.RequestStatus;
import com.example.nvt.model.Admin;
import com.example.nvt.model.Client;
import com.example.nvt.model.User;
import com.example.nvt.service.HouseholdRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class HouseholdRequestController {

    private final HouseholdRequestService householdRequestService;

    @PreAuthorize("hasAuthority('CLIENT')")
    @PostMapping(value = "/api/v1/realestate/{realestateId}/household/{householdId}/household-request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HouseholdDetailsDTO> createClaimRequest(@AuthenticationPrincipal Client client, @PathVariable Long realestateId,
                                                                  @PathVariable Long householdId, @RequestParam(value = "files") List<MultipartFile> files){
        HouseholdDetailsDTO details = householdRequestService.createClaimRequest(client, realestateId, householdId, files);
        return ResponseEntity.ok(details);
    }

    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
    @PutMapping("/api/v1/realestate/{realestateId}/household/{householdId}/household-request/{requestId}/accept")
    public ResponseEntity<HouseholdRequestDTO> acceptHouseholdRequest(@AuthenticationPrincipal Admin admin, @PathVariable Long realestateId,
                                       @PathVariable Long householdId, @PathVariable Long requestId){
        HouseholdRequestDTO dto = householdRequestService.acceptHouseholdRequest(admin, realestateId, householdId, requestId);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
    @PutMapping("/api/v1/realestate/{realestateId}/household/{householdId}/household-request/{requestId}/decline")
    public ResponseEntity<HouseholdRequestDTO> denyHouseholdRequest(@AuthenticationPrincipal Admin admin, @PathVariable Long realestateId,
                                       @PathVariable Long householdId, @PathVariable Long requestId, @RequestBody String denyReason){
        HouseholdRequestDTO dto = householdRequestService.denyHouseholdRequest(admin, realestateId, householdId, requestId, denyReason);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @GetMapping("/api/v1/realestate/{realestateId}/household/{householdId}/household-request/{requestId}/conflicted-pending-requests")
    public ResponseEntity<Page<HouseholdRequestPreviewDTO>> getConflictedPendingRequests(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable Long realestateId,
            @PathVariable Long householdId,
            @PathVariable Long requestId
    ) {
        Page<HouseholdRequestPreviewDTO> result = householdRequestService.getConflictedPendingRequests(realestateId, householdId, requestId, user, page, size);
        return ResponseEntity.ok(result);
    }


    @PreAuthorize("hasAnyAuthority('CLIENT','ADMIN', 'SUPERADMIN')")
    @GetMapping("/api/v1/household-request")
    public ResponseEntity<Page<HouseholdRequestPreviewDTO>> getClientRequests(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "requestSubmitted") String sortField,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Page<HouseholdRequestPreviewDTO> result = householdRequestService.getClientRequests(
                user.getId(), status, page, size, sortField, sortDir);
        return ResponseEntity.ok(result);
    }



    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'SUPERADMIN')")
    @GetMapping("/api/v1/realestate/{realestateId}/household/{householdId}/household-request/{requestId}")
    public ResponseEntity<HouseholdRequestDTO> getHouseholdRequestDetails(@AuthenticationPrincipal User user, @PathVariable Long realestateId,
                                           @PathVariable Long householdId, @PathVariable Long requestId){

        HouseholdRequestDTO dto = householdRequestService.getHouseholdRequestDetails( realestateId, householdId, requestId);
        return ResponseEntity.ok(dto);
    }




}
