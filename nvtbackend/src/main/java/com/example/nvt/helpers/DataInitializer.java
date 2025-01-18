package com.example.nvt.helpers;

import com.example.nvt.enumeration.Role;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.*;
import com.example.nvt.repository.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import kotlin.OptIn;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HouseholdRepository householdRepository;
    private final ClientRepository clientRepository;
    private final RealestateRepository realestateRepository;
    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final MunicipalityRepository municipalityRepository;

    @Override
    public void run(String... args) throws Exception {

        //regionRepository.deleteAll();
        //municipalityRepository.deleteAll();
        //cityRepository.deleteAll();
        //userRepository.deleteAll();
        //clientRepository.deleteAll();
        //householdRepository.deleteAll();
        //realestateRepository.deleteAll();

        initializeCities();


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

        initializeRealestates();



//        Realestate realestate1 = Realestate.builder()
//                .realestateOwner(client1)
//                .households(new ArrayList<>())
//                .build();
//        realestate1 = realestateRepository.save(realestate1);
//
//        Household household1 = Household.builder()
//                .isOnline(false)
//                .realEstate(realestate1)
//                .lastOnline(LocalDateTime.now().minusYears(100))
//                .build();
//        household1 = householdRepository.save(household1);
//
//        Household household2 = Household.builder()
//                .isOnline(false)
//                .realEstate(realestate1)
//                .lastOnline(LocalDateTime.now().minusYears(100))
//                .build();
//        household2 = householdRepository.save(household2);
//
//        Household household3 = Household.builder()
//                .isOnline(false)
//                .realEstate(realestate1)
//                .lastOnline(LocalDateTime.now().minusYears(100))
//                .build();
//        household3 = householdRepository.save(household3);
//
//        realestate1.getHouseholds().addAll(List.of(household1, household2, household3));
//        realestate1 = realestateRepository.save(realestate1);
//
//        client1.getRealEstates().add(realestate1);
//        client1 = clientRepository.save(client1);

    }

    private void initializeCities() {
        String filePath = "naselja_sr.csv";
        List<City> cities = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            boolean isFirstLine = true;

            Map<String, Long> regionMap = new HashMap<>();
            Map<String, Long> municipalityMap = new HashMap<>();

            while ((line = csvReader.readNext()) != null) {
//                if (isFirstLine) { // Skip the header
//                    isFirstLine = false;
//                    continue;
//                }


                String cityName = line[0];
                Long zipCode = Long.parseLong(line[1]);
                String regionName = line[2];
                String municipalityName = line[3];

                //System.out.println(cityName);
                //System.out.println("IKSDE1");
                City city = City.builder()
                        .zipCode(zipCode)
                        .name(cityName)
                        .build();
                //System.out.println(cityName);
                city = cityRepository.save(city);


               // System.out.println("IKSDE2");
                Municipality municipality;
                if(municipalityMap.containsKey(municipalityName)){

                    Long municipalityId = municipalityMap.get(municipalityName);
                    Optional<Municipality> municipalityWrapper = municipalityRepository.findById(municipalityId);
                    if(municipalityWrapper.isEmpty()) throw new InvalidInputException("Municipality not found");
                    municipality = municipalityWrapper.get();
                }else{
                    municipality = Municipality.builder()
                            .cities(new ArrayList<>())
                            .name(municipalityName)
                            .build();
                    municipality = municipalityRepository.save(municipality);
                    municipalityMap.put(municipalityName, municipality.getId());
                }

               // System.out.println("IKSDE3");
                city.setMunicipality(municipality);
                city = cityRepository.save(city);
//                municipality.getCities().add(city);
//                municipality = municipalityRepository.save(municipality);


               // System.out.println("IKSDE4");
                Region region;
                if(regionMap.containsKey(regionName)){
                    Long regionId = regionMap.get(regionName);
                    Optional<Region> regionWrapper = regionRepository.findById(regionId);
                    if(regionWrapper.isEmpty()) throw new InvalidInputException("Region not found");
                    region = regionWrapper.get();
                }else{
                    region = Region.builder()
                            .municipalities(new ArrayList<>())
                            .name(regionName)
                            .build();
                    region = regionRepository.save(region);
                    regionMap.put(regionName, region.getId());
                }


               // System.out.println("IKSDE5");
                municipality.setRegion(region);
                municipality = municipalityRepository.save(municipality);
                //System.out.println("IKSDE5.5");
//                region.getMunicipalities().add(municipality);
//                region = regionRepository.save(region);


                //System.out.println("IKSDE6");


            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        //return cityRepository.saveAll(cities);
    }

    private void  initializeRealestates(){
        String filePath = "../../../Downloads/kucni_br_csv/kucne_adrese.csv";
        List<Realestate> realestates = new ArrayList<>();

//        Optional<City> cityWrapper = cityRepository.findByName("Nova Crnja");
//        if(cityWrapper.isEmpty()) throw new RuntimeException("City not found");
//        City city = cityWrapper.get();

        Map<String, Long> cityMap = new HashMap<>();
        Map<String, Long> municipalityMap = new HashMap<>();

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            boolean isFirstLine = true;

            while ((line = csvReader.readNext()) != null) {
                if (isFirstLine) { // Skip the header
                    isFirstLine = false;
                    continue;
                }

                // Extract relevant parts
                String cityName = line[0];
                String municipalityName = line[1];
                Double lat = Double.parseDouble(line[2]);
                Double lon = Double.parseDouble(line[3]);
                String addressStreet = line[4];
                String addressNum = line[5];

                try{
                    Optional<City> cWrapper = cityRepository.findByNameAndMunName(cityName, municipalityName);
                    if(cWrapper.isEmpty()) {
                        if(cityMap.containsKey(cityName)){
                            cityMap.put(cityName, cityMap.get(cityName) + 1L);
                        }else{
                            System.out.println("NASELJE");
                            System.out.println(cityName + "-" + municipalityName + "\n");
                            cityMap.put(cityName, 1L);
                        }

                    }
                }catch (IncorrectResultSizeDataAccessException e){
                    System.out.println("CITY EXCEPTION");
                    System.out.println(cityName + "-" + municipalityName + "\n");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    return;
                }


                try{
                    Optional<Municipality> mWrapper = municipalityRepository.findByNameAndCityName(municipalityName, cityName);
                    if(mWrapper.isEmpty()) {
                        if(municipalityMap.containsKey(municipalityName)){
                            municipalityMap.put(municipalityName, municipalityMap.get(municipalityName) + 1L);
                        }else{
                            System.out.println("OPSTINA");
                            System.out.println(cityName + "-" + municipalityName + "\n");
                            municipalityMap.put(municipalityName, 1L);
                        }
                    }
                }catch (IncorrectResultSizeDataAccessException e){
                    System.out.println("MUNICIPALITY EXCEPTION");
                    System.out.println(cityName + "-" + municipalityName + "\n");
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                    return;
                }




//                Realestate realestate = Realestate.builder()
//                        .city(city)
//                        .lat(lat)
//                        .lon(lon)
//                        .addressStreet(addressStreet)
//                        .addressNum(addressNum)
//                        .build();
                //realestates.add(realestate);
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        System.out.println("ZAVRSILI SMO");
//        System.out.println(cityMap);
//        System.out.println(municipalityMap);
        //realestateRepository.saveAll(realestates);
    }


}