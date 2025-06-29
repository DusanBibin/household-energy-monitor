package com.example.nvt.controller;


import com.example.nvt.DTO.*;
import com.example.nvt.rabbitmq.RabbitMQSender;
import com.example.nvt.helpers.ResponseMessage;
import com.example.nvt.model.User;
import com.example.nvt.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private final RabbitMQSender rabbitMQSender;

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@Valid @RequestBody AuthRequestDTO request, HttpServletResponse response){
        PartialUserDataDTO token = authService.authenticate(request, response);

        return ResponseEntity.ok(token);
    }


    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@Valid @RequestPart(value = "formData", required = true) RegisterRequestDTO request,
                                      @Valid @RequestPart(value = "profileImage", required = true) MultipartFile profileImage,
                                      @AuthenticationPrincipal User user){

        String message = authService.register(request, profileImage, user);
        return ResponseEntity.ok(new ResponseMessage(message));

    }


    @GetMapping(value = "/activate/{idActivation}")
    public ResponseEntity<?> activateUserEmail(@PathVariable("idActivation") String verificationCode) {

        authService.verifyUser(verificationCode);
        return ResponseEntity.ok(new ResponseMessage("Account activated"));
    }

    
    @PutMapping("/change-superadmin-password")
    @PreAuthorize("hasAuthority('SUPERADMIN')")
    public ResponseEntity<?> changeSuperadminPassword(@Valid @RequestBody SuperadminPasswordChangeDTO request,
                                                      @AuthenticationPrincipal User user){

        authService.changeSuperadminPassword(request, user);

        return ResponseEntity.ok(new ResponseMessage("Password changed successfully"));
    }

//    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @PostMapping("/logout")
    public void logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        //jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Expire immediately
        jwtCookie.setAttribute("SameSite", "Strict");

        response.addCookie(jwtCookie);
        System.out.println("izlogovali smo se ");
    }

    @GetMapping("/is-authenticated")
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser")));
        return authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"));
    }

    
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN', 'OFFICIAL', 'SUPERADMIN')")
    @GetMapping("/kurac-auth")
    public ResponseEntity<String> kurac(){
        System.out.println("Kurac auth");
        return ResponseEntity.ok("Kurac auth");
    }

    @GetMapping("/kurac-unauth")
    public ResponseEntity<String> kuracUnauthorized(){
        System.out.println("Kurac unauth");
        return ResponseEntity.ok("Kurac unauth");
    }





}
