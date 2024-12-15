package com.example.nvt.configuration;


import com.example.nvt.helpers.ResponseMessage;
import com.example.nvt.model.User;
import com.example.nvt.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @GetMapping(value = "/profile-img")
    public ResponseEntity<?> getProfileImage(@AuthenticationPrincipal User user) {

        String filePath = fileService.getProfileImg(user.getId());
        Resource resource = fileService.getFileResource(filePath);
        MediaType mediaType = fileService.getFileMediaType(filePath);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(resource);

    }

}
