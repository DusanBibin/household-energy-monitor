package com.example.nvt.controller;


import com.example.nvt.exceptions.InvalidAuthenticationException;
import com.example.nvt.model.User;
import com.example.nvt.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

//    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping(value = "/profile-img")
    public ResponseEntity<?> getProfileImage(@AuthenticationPrincipal User user) {


        if(user == null) throw new InvalidAuthenticationException("Not Authenticated");

        System.out.println("PROFILEIMG");
        String filePath = fileService.getSmallProfileImg(user.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Accel-Redirect", filePath);
        System.out.println(filePath);

        System.out.println("USPESNO SMO VRATILI PROFILE-IMG");
        return ResponseEntity.status(HttpStatus.OK).headers(headers).build();

    }

}
