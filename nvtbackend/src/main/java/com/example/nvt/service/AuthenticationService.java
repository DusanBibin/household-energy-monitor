package com.example.nvt.service;


import com.example.nvt.DTO.*;
import com.example.nvt.enumeration.Role;
import com.example.nvt.exceptions.EmailNotConfirmedException;
import com.example.nvt.exceptions.InvalidAuthenticationException;
import com.example.nvt.exceptions.InvalidAuthorizationException;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.*;
import com.example.nvt.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final FileService fileService;
    private final ClientService clientService;
    private final UserRepository userRepository;

    public PartialUserDataDTO authenticate(AuthRequestDTO request, HttpServletResponse response) {
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

        if(!user.isEmailConfirmed() ) throw new InvalidAuthorizationException("Email not confirmed for this user");

        var jwtToken = jwtService.generateToken(user, user.getId());

        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(60 * 60 * 24); // PROMENITI I ZA JWT VREME ( VREME MORA BITI ISTO ZA OBA)
        jwtCookie.setAttribute("SameSite", "None"); // Prevent CSRF attacks

        response.addCookie(jwtCookie);

        PartialUserDataDTO data = PartialUserDataDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getFirstName())
                .lastname(user.getLastname())
                .role(user.getRole())
                .build();

        if(user instanceof SuperAdmin superAdmin){
            data.setFirstLogin(superAdmin.isFirstLogin());
        }
        System.out.println(data);
        return data;
    }


    public void  changeSuperadminPassword(SuperadminPasswordChangeDTO request, User user) {


        if(!request.getNewPassword().equals(request.getRepeatPassword()))
            throw new InvalidInputException("Passwords do not match");

        if(passwordEncoder.matches(request.getNewPassword(), user.getPassword()))
            throw new InvalidAuthorizationException("You cannot set the previous password to be current one");

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

    @Transactional
    public String register(@Valid RegisterRequestDTO request, MultipartFile profileImage, User user) {

        if(!(user instanceof SuperAdmin superAdmin || user == null))
            throw new InvalidAuthorizationException("Invalid action");

        boolean emailConfirmed = false;
        Role role = Role.CLIENT;

        System.out.println("Neka registracija se dogadja");
        if(user instanceof SuperAdmin superAdmin){
            emailConfirmed = true;
            role = Role.ADMIN;
        }

        Random random = new Random();

        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[16];
        secureRandom.nextBytes(randomBytes);

        String code = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);

        if(!request.getPassword().equals(request.getRepeatPassword()))
            throw new InvalidInputException("Passwords do not match");

        if(userService.emailAlreadyExists(request.getEmail())) throw new InvalidInputException("User with this email already exists");



        User newUser;

        if (role == Role.ADMIN) {
            newUser = Admin.builder()
                    .firstName(request.getName().substring(0, 1).toUpperCase() + request.getName().substring(1) )
                    .lastname(request.getLastname().substring(0, 1).toUpperCase() + request.getLastname().substring(1))
                    .email(request.getEmail().toLowerCase())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNumber(request.getPhone())
                    .emailConfirmed(emailConfirmed)
                    .role(role)
                    .build();
        } else {
            newUser = Client.builder()
                    .firstName(request.getName().substring(0, 1).toUpperCase() + request.getName().substring(1) )
                    .lastname(request.getLastname().substring(0, 1).toUpperCase() + request.getLastname().substring(1))
                    .email(request.getEmail().toLowerCase())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .phoneNumber(request.getPhone())
                    .emailConfirmed(emailConfirmed)
                    .role(role)
                    .build();
        }


        newUser = userService.saveUser(newUser);

        String filePath = "";
        try {
            filePath = fileService.saveProfileImg(profileImage, newUser.getId());
        }catch (Exception e){
            throw new InvalidInputException("Profile image is invalid");
        }

        newUser.setVerification(new Verification(code, LocalDateTime.now().plusDays(1)));
        newUser.setProfileImg(filePath);
        userService.saveUser(newUser);

        String message = "Registration successful. Validation email sent to ".concat(request.getEmail());
        if(!emailConfirmed) emailService.sendVerificationEmail(newUser);
        else message = "Admin registration successful";

        return message;
    }

    public String verifyUser(String verificationCode) {

        var client = clientService.findClientByValidValidation(verificationCode);
        if(client.isEmailConfirmed()) throw new InvalidInputException("This user is already verified");
        client.setEmailConfirmed(true);
        userRepository.save(client);

        return client.getEmail();
    }
}