package com.example.nvt.service;


import com.example.nvt.DTO.AuthRequestDTO;
import com.example.nvt.DTO.AuthResponseDTO;
import com.example.nvt.DTO.SuperadminPasswordChangeDTO;
import com.example.nvt.exceptions.EmailNotConfirmedException;
import com.example.nvt.exceptions.InvalidAuthenticationException;
import com.example.nvt.exceptions.InvalidAuthorizationException;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.SuperAdmin;
import com.example.nvt.model.User;
import com.example.nvt.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        System.out.println(request);
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            System.out.println(e.getMessage());
            throw new InvalidAuthenticationException("Email or password is invalid");
        }

        var user = userService.getUserByEmail(request.getEmail());

        if(!user.isEmailConfirmed() ) throw new EmailNotConfirmedException("Email not confirmed for this user");

        var jwtToken = jwtService.generateToken(user, user.getId());
        if(user instanceof SuperAdmin superAdmin){

            return AuthResponseDTO.builder()
                    .token(jwtToken)
                    .build();

        }
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }


    public void changeSuperadminPassword(SuperadminPasswordChangeDTO request, User user) {




        if(!request.getNewPassword().equals(request.getRepeatPassword()))
            throw new InvalidInputException("Passwords do not match");

        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new InvalidInputException("You cannot set the previous password to be current one");

        if(user instanceof SuperAdmin superAdmin){
            if(!superAdmin.isFirstLogin())
                throw new InvalidAuthorizationException("You already changed your temporary password");
        }

        String newPassword = passwordEncoder.encode(request.getNewPassword());

        SuperAdmin superAdmin = (SuperAdmin) user;
        superAdmin.setFirstLogin(false);
        superAdmin.setPassword(newPassword);
        userService.saveUser(user);

    }
}