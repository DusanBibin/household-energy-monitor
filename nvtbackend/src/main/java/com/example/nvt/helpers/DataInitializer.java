package com.example.nvt.helpers;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest;
import co.elastic.clients.elasticsearch.indices.GetIndexRequest;
import com.example.nvt.enumeration.Role;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.*;
import com.example.nvt.model.elastic.RealestateDoc;
import com.example.nvt.repository.*;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final int BATCH_SIZE = 5000;

    @PersistenceContext
    private EntityManager entityManager;


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HouseholdRepository householdRepository;
    private final ClientRepository clientRepository;
    private final RealestateRepository realestateRepository;
    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final MunicipalityRepository municipalityRepository;

    private final RealestateDocRepository realestateDocRepository;
    private final ElasticsearchClient elasticsearchClient;

    private StopWatch stopWatch = new StopWatch();
    @Override
    public void run(String... args) throws Exception {

        //regionRepository.deleteAll();
        //municipalityRepository.deleteAll();
        //cityRepository.deleteAll();
        //userRepository.deleteAll();
        //clientRepository.deleteAll();
        //householdRepository.deleteAll();
        //realestateRepository.deleteAll();

        initCitiesMunicipalitiesRegions();




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

        initRealestates();

        System.out.println(stopWatch.prettyPrint());

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

    @Transactional
    protected void initCitiesMunicipalitiesRegions() {
        stopWatch.start("Initializing cities");
        System.out.println("Initializing Cities...");

        String filePath = "naselja.csv";
        List<City> cities = new ArrayList<>();
        Map<String, Region> regionMap = regionRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Region::getName, Function.identity()));

        Map<String, Municipality> municipalityMap = municipalityRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Municipality::getName, Function.identity()));

        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            boolean isFirstLine = true;

            while ((line = csvReader.readNext()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String cityName = line[0];
                Long zipCode = Long.parseLong(line[1]);
                String regionName = line[2];
                String municipalityName = line[3];

                // Get or create Region
                Region region = regionMap.computeIfAbsent(regionName, name -> {
                    Region newRegion = Region.builder().name(name).build();
                    regionRepository.save(newRegion);
                    return newRegion;
                });

                // Get or create Municipality
                Municipality municipality = municipalityMap.computeIfAbsent(municipalityName, name -> {
                    Municipality newMunicipality = Municipality.builder()
                            .name(name)
                            .region(region)
                            .build();
                    municipalityRepository.save(newMunicipality);
                    return newMunicipality;
                });

                // Create City
                City city = City.builder()
                        .name(cityName)
                        .zipCode(zipCode)
                        .municipality(municipality)
                        .build();

                cities.add(city);

                if (cities.size() >= BATCH_SIZE) {
                    cityRepository.saveAll(cities);
                    cities.clear();
                }
            }

            // Save remaining cities
            if (!cities.isEmpty()) {
                cityRepository.saveAll(cities);
            }

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        stopWatch.stop();
        System.out.println("DONE in " + stopWatch.getTotalTimeSeconds() + " seconds");
    }


    @Transactional
    protected void initRealestates() {
        System.out.println("Initializing Realestates...");
        stopWatch.start("Initializing Realestates");
        String filePath = "../../../Downloads/kucni_br_csv/kucne_adrese_lite.csv";

        // Preload all cities into memory
        Map<String, City> cityMap = cityRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        city -> city.getName() + "_" + city.getMunicipality().getName(),
                        city -> city
                ));

        List<Realestate> realestates = new ArrayList<>();

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

                // Lookup city
                City city = cityMap.get(cityName + "_" + municipalityName);
                if (city == null) {
                    System.out.println("City not found: " + cityName + ", " + municipalityName);
                    continue; // Skip if city is not found
                }

                // Create Realestate object
                Realestate realestate = Realestate.builder()
                        .city(city)
                        .lat(lat)
                        .lon(lon)
                        .addressStreet(addressStreet)
                        .addressNum(addressNum)
                        .build();
                realestates.add(realestate);

                // Save in batches
                if (realestates.size() >= BATCH_SIZE) {
                    realestateRepository.saveAll(realestates);


                    List<RealestateDoc> realestateDocs = realestates.stream()
                                    .map(r -> {
                                            City c = r.getCity();
                                            Municipality m = c.getMunicipality();
                                            Region reg = m.getRegion();

                                            String fullAddress = r.getAddressStreet() + " " + r.getAddressNum() + " " + c.getZipCode().toString()
                                                + " " + c.getName() + " " + m.getName() + " " + reg.getName();
                                            return RealestateDoc.builder()
                                                    .dbId(r.getId())
                                                    .address(r.getAddressStreet() + " " + r.getAddressNum())
                                                    .zipcode(c.getZipCode().toString())
                                                    .city(c.getName())
                                                    .municipality("Opština " + m.getName())
                                                    .region(reg.getName())
                                                    .fullAddress(fullAddress)
                                                    .lat(r.getLat())
                                                    .lon(r.getLon())
                                                    .build(); })
                                    .toList();
                    realestateDocRepository.saveAll(realestateDocs);
                    realestates.clear();
                }
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

        // Save remaining records
        if (!realestates.isEmpty()) {
            realestateRepository.saveAll(realestates);
            List<RealestateDoc> realestateDocs = realestates.stream()
                    .map(r -> {
                        City c = r.getCity();
                        Municipality m = c.getMunicipality();
                        Region reg = m.getRegion();
                        String fullAddress = r.getAddressStreet() + " " + r.getAddressNum() + " " + c.getZipCode().toString()
                                + " " + c.getName() + " " + m.getName() + " " + reg.getName();
                        return RealestateDoc.builder()
                                .dbId(r.getId())
                                .address(r.getAddressStreet() + " " + r.getAddressNum())
                                .zipcode(c.getZipCode().toString())
                                .city(c.getName())
                                .municipality("Opština " + m.getName())
                                .region(reg.getName())
                                .fullAddress(fullAddress)
                                .lat(r.getLat())
                                .lon(r.getLon())
                                .build(); })
                    .toList();
            realestateDocRepository.saveAll(realestateDocs);
            realestates.clear();
        }

        stopWatch.stop();
        System.out.println("DONE in " + stopWatch.getTotalTimeSeconds() + " seconds");
    }



}