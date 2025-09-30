package com.example.nvt.controller;


import com.example.nvt.DTO.PartialUserDataDTO;
import com.example.nvt.exceptions.InvalidAuthenticationException;
import com.example.nvt.model.SuperAdmin;
import com.example.nvt.model.User;
import com.example.nvt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

//    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping(value = "/partial-data")
    public ResponseEntity<?> getUserData(@AuthenticationPrincipal User user) {

        if(user == null) throw new InvalidAuthenticationException("Not Authenticated");

        return ResponseEntity.ok(userService.getPartialUserData(user.getId()));
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping(value = "/{userId}")
    public ResponseEntity<?> getSummaryData(@PathVariable Long userId) {

        return ResponseEntity.ok(userService.getPartialUserData(userId));

    }





}
