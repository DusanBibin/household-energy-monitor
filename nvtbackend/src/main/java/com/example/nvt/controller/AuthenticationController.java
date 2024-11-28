package com.example.nvt.controller;


import com.example.nvt.DTO.AuthRequestDTO;
import com.example.nvt.DTO.AuthResponseDTO;
import com.example.nvt.DTO.SuperadminPasswordChangeDTO;
import com.example.nvt.helpers.ResponseMessage;
import com.example.nvt.model.User;
import com.example.nvt.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthRequestDTO request){
        AuthResponseDTO token = authService.authenticate(request);
        return ResponseEntity.ok(token);
    }


    @PutMapping("/change-superadmin-password")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<?> changeSuperadminPassword(@Valid @RequestBody SuperadminPasswordChangeDTO request,
                                                      @AuthenticationPrincipal User user){

        authService.changeSuperadminPassword(request, user);

        return ResponseEntity.ok(new ResponseMessage("Password changed successfully"));
    }




}
