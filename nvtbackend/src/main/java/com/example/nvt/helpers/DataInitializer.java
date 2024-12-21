package com.example.nvt.helpers;

import com.example.nvt.enumeration.Role;
import com.example.nvt.model.*;
import com.example.nvt.repository.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HouseholdRepository householdRepository;
    private final ClientRepository clientRepository;
    private final RealestateRepository realestateRepository;
    private final CityRepository cityRepository;

    @Override
    public void run(String... args) throws Exception {

        cityRepository.deleteAll();
        userRepository.deleteAll();
        clientRepository.deleteAll();
        householdRepository.deleteAll();
        var cities = initializeCities();

        System.out.println(userRepository.getAllUsers().size());
        System.out.println(clientRepository.getAllClients().size());
        System.out.println(householdRepository.getAllHouseholds().size());

        String superAdminMail = "admin";
        SuperAdmin superAdmin = SuperAdmin.builder()
                .email(superAdminMail)
                .firstName("Ime")
                .lastname("Prezime")
                .phoneNumber("0691817839")
                .password(passwordEncoder.encode(PasswordGenerator.generatePassword(superAdminMail, "password.txt", 30)))
                .verification(new Verification())
                .profileImg("dijamantmann.jpg")
                .emailConfirmed(true)
                .firstLogin(true)
                .role(Role.SUPERADMIN).build();
        userRepository.save(superAdmin);


        Admin admin1 = Admin.builder()
                .email("admin1@gmail.com")
                .firstName("Ime")
                .lastname("Prezime")
                .phoneNumber("0691817839")
                .password(passwordEncoder.encode("admin1"))
                .verification(new Verification())
                .profileImg("dijamantmann.jpg")
                .emailConfirmed(true)
                .role(Role.ADMIN).build();
        userRepository.save(admin1);


        Client client1 = Client.builder()
                .email("client1@gmail.com")
                .firstName("Ime")
                .lastname("Prezime")
                .phoneNumber("0691817839")
                .password(passwordEncoder.encode("admin1"))
                .verification(new Verification())
                .profileImg("dijamantmann.jpg")
                .emailConfirmed(true)
                .role(Role.CLIENT)
                .realEstates(new ArrayList<>())
                .build();
        client1 = clientRepository.save(client1);


        Realestate realestate1 = Realestate.builder()
                .client(client1)
                .households(new ArrayList<>())
                .build();
        realestate1 = realestateRepository.save(realestate1);

        Household household1 = Household.builder()
                .isOnline(false)
                .realEstate(realestate1)
                .lastOnline(LocalDateTime.now().minusYears(100))
                .build();
        household1 = householdRepository.save(household1);

        Household household2 = Household.builder()
                .isOnline(false)
                .realEstate(realestate1)
                .lastOnline(LocalDateTime.now().minusYears(100))
                .build();
        household2 = householdRepository.save(household2);

        Household household3 = Household.builder()
                .isOnline(false)
                .realEstate(realestate1)
                .lastOnline(LocalDateTime.now().minusYears(100))
                .build();
        household3 = householdRepository.save(household3);

        realestate1.getHouseholds().addAll(List.of(household1, household2, household3));
        realestate1 = realestateRepository.save(realestate1);

        client1.getRealEstates().add(realestate1);
        client1 = clientRepository.save(client1);

    }

    private List<City> initializeCities() {
        String filePath = "rs.csv";
        List<City> cities = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            boolean isFirstLine = true;

            while ((line = csvReader.readNext()) != null) {
                if (isFirstLine) { // Skip the header
                    isFirstLine = false;
                    continue;
                }

                // Extract relevant parts
                String name = line[0];
                Double lat = Double.parseDouble(line[1]);
                Double lon = Double.parseDouble(line[2]);

                // Create City instance and add it to the list
                cities.add(new City(null, name, lat, lon));
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        return cityRepository.saveAll(cities);
    }


}