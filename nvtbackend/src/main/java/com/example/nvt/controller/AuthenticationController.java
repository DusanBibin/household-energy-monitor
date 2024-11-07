package com.example.nvt.controller;


import com.example.nvt.DTO.AuthenticationRequestDTO;
import com.example.nvt.DTO.AuthenticationResponseDTO;
import com.example.nvt.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {


    private final AuthenticationService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthenticationRequestDTO request){

        AuthenticationResponseDTO token = authService.authenticate(request);

        return ResponseEntity.ok(token);
    }




}
