package com.example.nvt.controller;


import com.example.nvt.DTO.UserSummaryDTO;
import com.example.nvt.model.Client;
import com.example.nvt.service.ClerkService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClerkController {

    private final ClerkService clerkService;

    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/clerk")
    public ResponseEntity<Page<UserSummaryDTO>> getClerks(@AuthenticationPrincipal Client client,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size){
        Page<UserSummaryDTO> clerks = clerkService.getClerks( page, size);
        return ResponseEntity.ok(clerks);
    }

}
