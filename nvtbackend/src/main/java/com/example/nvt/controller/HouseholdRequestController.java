package com.example.nvt.controller;


import com.example.nvt.model.Client;
import com.example.nvt.service.HouseholdRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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

    @PreAuthorize("hasAnyAuthority('CLIENT')")
    @PostMapping(value = "/api/v1/realestate/{realestateId}/household/{householdId}/household-request", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void createClaimRequest(@AuthenticationPrincipal Client client, @PathVariable Long realestateId,
                                   @PathVariable Long householdId, @RequestParam(value = "files") List<MultipartFile> files){
        householdRequestService.createClaimRequest(client, realestateId, householdId, files);
    }


}
