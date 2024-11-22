package com.example.nvt.helpers;

import com.example.nvt.enumeration.Role;
import com.example.nvt.model.Admin;
import com.example.nvt.model.SuperAdmin;
import com.example.nvt.model.User;
import com.example.nvt.model.Verification;
import com.example.nvt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {



        userRepository.deleteAll();

        String superAdminMail = "admin";
        SuperAdmin superAdmin = SuperAdmin.builder()
                .email(superAdminMail)
                .firstName("ime")
                .lastname("prezime")
                .phoneNumber("0691817839")
                .password(passwordEncoder.encode(PasswordGenerator.generatePassword(superAdminMail, "password.txt", 30)))
                .verification(new Verification())
                .profileImgLocation("iksde")
                .emailConfirmed(true)
                .firstLogin(true)
                .role(Role.SUPERADMIN).build();

        userRepository.save(superAdmin);


        Admin admin1 = Admin.builder()
                .email("admin1@gmail.com")
                .firstName("ime")
                .lastname("prezime")
                .phoneNumber("0691817839")
                .password(passwordEncoder.encode("admin1"))
                .verification(new Verification())
                .profileImgLocation("iksde")
                .emailConfirmed(true)
                .role(Role.ADMIN).build();


        userRepository.save(admin1);


        List<User> users = userRepository.findAll();
        System.out.println(users.size());

        for(User user : users) {
            System.out.println(user.getEmail());
        }
    }
}