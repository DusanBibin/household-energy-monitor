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
        System.out.println("GETUSERDATA");

        if(user == null) throw new InvalidAuthenticationException("Not Authenticated");

        PartialUserDataDTO data = PartialUserDataDTO.builder()
                .email(user.getEmail())
                .name(user.getFirstName())
                .lastname(user.getLastname())
                .role(user.getRole())
                .build();

        if(user instanceof SuperAdmin superAdmin){
            data.setFirstLogin(superAdmin.isFirstLogin());
        }
        System.out.println("USPESNO SMO VRATILI PARTIAL DATA");
        return ResponseEntity.ok().body(data);
    }
}
