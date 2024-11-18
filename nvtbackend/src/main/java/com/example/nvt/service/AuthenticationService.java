package com.example.nvt.service;


import com.example.nvt.DTO.AuthRequestDTO;
import com.example.nvt.DTO.AuthResponseDTO;
import com.example.nvt.exceptions.EmailNotConfirmedException;
import com.example.nvt.exceptions.InvalidAuthenticationException;
import com.example.nvt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        }catch (AuthenticationException e){
            throw new InvalidAuthenticationException("Email or password is invalid");
        }



        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        if(!user.isEmailConfirmed() ) throw new EmailNotConfirmedException("Email not confirmed for this user");


        var jwtToken = jwtService.generateToken(user, user.getId());
        return AuthResponseDTO.builder()
                .token(jwtToken)
                .build();
    }


}