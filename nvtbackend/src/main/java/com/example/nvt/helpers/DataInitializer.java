package com.example.nvt.helpers;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.example.nvt.enumeration.RealEstateType;
import com.example.nvt.enumeration.Role;
import com.example.nvt.model.*;
import com.example.nvt.model.elastic.CityDoc;
import com.example.nvt.model.elastic.MunicipalityDoc;
import com.example.nvt.model.elastic.RealestateDoc;
import com.example.nvt.model.elastic.RegionDoc;
import com.example.nvt.repository.*;
import com.example.nvt.repository.elastic.CityDocRepository;
import com.example.nvt.repository.elastic.MunicipalityDocRepository;
import com.example.nvt.repository.elastic.RealestateDocRepository;
import com.example.nvt.repository.elastic.RegionDocRepository;
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
    private final RegionDocRepository regionDocRepository;
    private final MunicipalityDocRepository municipalityDocRepository;
    private final CityDocRepository cityDocRepository;
    private final ElasticsearchClient elasticsearchClient;



    private StopWatch stopWatch = new StopWatch();



    private Map<Long, String> regionDocMap = new HashMap<>();
    private Map<Long, String> municipalityDocMap = new HashMap<>();
    private Map<Long, String> cityDocMap = new HashMap<>();


    String passwordUniversal;
    @Override
    public void run(String... args) throws Exception {

        passwordUniversal = passwordEncoder.encode("clientuniversal");
        initCitiesMunicipalitiesRegions();




        String superAdminMail = "admin";
        SuperAdmin superAdmin = SuperAdmin.builder()
                .email(superAdminMail)
                .firstName("Ime")
                .lastname("Prezime")
                .phoneNumber("0695817839")
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
                .phoneNumber("0692817839")
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
                .password(passwordEncoder.encode("client1"))
                .verification(new Verification())
                .profileImg("dijamantmann.jpg")
                .emailConfirmed(true)
                .role(Role.CLIENT)
                .build();
        client1 = clientRepository.save(client1);

        Client client2 = Client.builder()
                .email("client2@gmail.com")
                .firstName("Ime")
                .lastname("Prezime")
                .phoneNumber("0697817839")
                .password(passwordEncoder.encode("client2"))
                .verification(new Verification())
                .profileImg("NEMA")
                .emailConfirmed(true)
                .role(Role.CLIENT)
                .build();
        client2 = clientRepository.save(client2);

        initRealestates();
        //initClients();
        System.out.println(stopWatch.prettyPrint());


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

                Region region = regionMap.computeIfAbsent(regionName, name -> {
                    Region newRegion = Region.builder().name(name).build();
                    newRegion = regionRepository.save(newRegion);
                    RegionDoc doc = RegionDoc.builder()
                            .dbId(newRegion.getId())
                            .region(newRegion.getName())
                            .build();
                    doc = regionDocRepository.save(doc);
                    if(!regionDocMap.containsKey(newRegion.getId())) regionDocMap.put(newRegion.getId(), doc.getId());
                    return newRegion;
                });

                Municipality municipality = municipalityMap.computeIfAbsent(municipalityName, name -> {
                    Municipality newMunicipality = Municipality.builder()
                            .name(name)
                            .region(region)
                            .build();
                    newMunicipality = municipalityRepository.save(newMunicipality);

                    MunicipalityDoc doc = MunicipalityDoc.builder()
                            .dbId(newMunicipality.getId())
                            .municipality(newMunicipality.getName() + ", " + region.getName())
                            .build();
                    doc = municipalityDocRepository.save(doc);
                    if(!municipalityDocMap.containsKey(newMunicipality.getId())) municipalityDocMap.put(newMunicipality.getId(), doc.getId());
                    return newMunicipality;
                });

                City city = City.builder()
                        .name(cityName)
                        .zipCode(zipCode)
                        .municipality(municipality)
                        .build();

                cities.add(city);

                if (cities.size() >= BATCH_SIZE) {

                    cityRepository.saveAll(cities);
                    List<CityDoc> citiesDocs = cities.stream()
                                    .map(c -> {
                                            var m = c.getMunicipality();
                                            var r = m.getRegion();
                                            var doc = CityDoc.builder()
                                            .dbId(c.getId())
                                            .city(c.getName() + ", " + m.getName() + ", " + r.getName())
                                            .build();
                                            return doc;
                                        }
                                    ).toList();
                    Iterable<CityDoc> cityDocsIter = cityDocRepository.saveAll(citiesDocs);

                    for(CityDoc cityDoc : cityDocsIter){
                        cityDocMap.put(cityDoc.getDbId(), cityDoc.getId());
                    }

                    cities.clear();
                }
            }

            // Save remaining cities
            if (!cities.isEmpty()) {
                cities = cityRepository.saveAll(cities);
                List<CityDoc> citiesDocs = cities.stream()
                        .map(c -> {
                                    var m = c.getMunicipality();
                                    var r = m.getRegion();
                                    var doc = CityDoc.builder()
                                            .dbId(c.getId())
                                            .city(c.getName() + ", " + m.getName() + ", " + r.getName())
                                            .build();
                                    return doc;
                                }
                        ).toList();
                Iterable<CityDoc> cityDocsIter = cityDocRepository.saveAll(citiesDocs);

                for(CityDoc cityDoc : cityDocsIter){
                    cityDocMap.put(cityDoc.getDbId(), cityDoc.getId());
                }

                cities.clear();
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

        Random random = new Random();

        // Preload all cities into memory
        Map<String, City> cityMap = cityRepository.findAll()
                .stream()
                .collect(Collectors.toMap(
                        city -> city.getName() + "_" + city.getMunicipality().getName(),
                        city -> city
                ));

        List<Realestate> realestates = new ArrayList<>();

        Long clientCounter = 3L;
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
                City city = cityMap.get(cityName + "_Opština " + municipalityName);
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
                        .type(getRandomRealEstateType())
                        .households(new ArrayList<>())
                        .build();
                realestates.add(realestate);

                switch (realestate.getType()){
                    case HOUSE -> {
                        Client clientH = null;
                        if(random.nextLong() > 10L){
                            clientH = createClient(clientCounter);
                        }

                        Household household = Household.builder()
                                .size(random.nextDouble(71) + 40)
                                .authorizedViewers(new ArrayList<>())
                                .householdOwner(clientH)
                                .realestate(realestate)
                                .build();

                        clientCounter++;

                        realestate.setTotalFloors(random.nextLong(1) + 3);
                        realestate.getHouseholds().add(household);
                    }
                    case COTTAGE -> {
                        Client clientH = null;
                        if(random.nextLong() > 10L){
                            clientH = createClient(clientCounter);
                        }

                        Household household = Household.builder()
                                .size(random.nextDouble(100) + 100)
                                .authorizedViewers(new ArrayList<>())
                                .householdOwner(clientH)
                                .realestate(realestate)
                                .build();

                        clientCounter++;

                        realestate.setTotalFloors(1L);
                        realestate.getHouseholds().add(household);
                    }
                    case BUILDING -> {
                        Long totalFloors = getBuildingStories();
                        Long apartmentsPerFloor = random.nextLong(20) + 2;
                        Double apartmentSize = random.nextDouble(60) + 10;

                        Long counter = 0L;
                        for(int i = 0; i < totalFloors; i++) {
                            for (int j = 0; j < apartmentsPerFloor; j++) {
                                counter++;

                                Client clientH = null;
                                if(random.nextLong() > 10L){
                                    clientH = createClient(clientCounter);
                                }

                                Household household = Household.builder()
                                        .size(apartmentSize)
                                        .apartmentNum(counter)
                                        .authorizedViewers(new ArrayList<>())
                                        .householdOwner(clientH)
                                        .realestate(realestate)
                                        .build();

                                clientCounter++;
                                realestate.getHouseholds().add(household);

                            }
                        }
                        realestate.setTotalFloors(totalFloors);
                        realestate.setApartmentPerFloorNum(apartmentsPerFloor);

                    }
                    case COMMERCE_SPACE -> {
                        Client clientH = null;
                        if(random.nextLong() > 10L){
                            clientH = createClient(clientCounter);
                        }

                        Household household = Household.builder()
                                .size(random.nextDouble(230) + 20)
                                .authorizedViewers(new ArrayList<>())
                                .realestate(realestate)
                                .householdOwner(clientH)
                                .build();

                        clientCounter++;
                        realestate.setTotalFloors(random.nextLong(1) + 1);
                        realestate.getHouseholds().add(household);
                    }
                }

                // Save in batches
                if (realestates.size() >= BATCH_SIZE) {
                    realestates = realestateRepository.saveAll(realestates);


                    List<RealestateDoc> realestateDocs = realestates.stream()
                                    .map(r -> {



                                            City c = r.getCity();
                                            Municipality m = c.getMunicipality();
                                            Region reg = m.getRegion();

                                            String fullAddress = r.getAddressStreet() + " " + r.getAddressNum() + " " + c.getZipCode().toString()
                                                + " " + c.getName() + " " + m.getName() + " " + reg.getName();
                                            return RealestateDoc.builder()
                                                    .dbId(r.getId())
                                                    .address(fullAddress)
                                                    .type(r.getType())
                                                    .cityDocId(cityDocMap.get(c.getId()))
                                                    .municipalityDocId(municipalityDocMap.get(m.getId()))
                                                    .regionDocId(regionDocMap.get(reg.getId()))
                                                    .location(r.getLat() + "," + r.getLon())
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
                                .address(fullAddress)
                                .type(r.getType())
                                .cityDocId(cityDocMap.get(c.getId()))
                                .municipalityDocId(municipalityDocMap.get(m.getId()))
                                .regionDocId(regionDocMap.get(reg.getId()))
                                .location(r.getLat() + "," + r.getLon())
                                .build(); })
                    .toList();
            realestateDocRepository.saveAll(realestateDocs);
            realestates.clear();
        }
        stopWatch.stop();
        System.out.println("DONE in " + stopWatch.getTotalTimeSeconds() + " seconds");

    }


    private Client createClient(Long clientNum){
        Random random = new Random();
        Client client = null;
        //if(random.nextLong(100L) >= 10){
            client = Client.builder()
                    .email("client" + clientNum + "@example.com")
                    .firstName("Ime" + clientNum)
                    .lastname("Prezime" + clientNum)
                    .phoneNumber("0697817839")
                    .password(passwordUniversal)
                    .profileImg("NEMA")
                    .emailConfirmed(true)
                    .realEstates(new ArrayList<>())
                    .realEstateRequests(new ArrayList<>())
                    .households(new ArrayList<>())
                    .role(Role.CLIENT)
                    .build();
        //}
        return client;
    }


    private static RealEstateType getRandomRealEstateType() {
        TreeMap<Double, RealEstateType> map = new TreeMap<>();
        Random random = new Random();

        // Defining the probabilities
        map.put(70.0, RealEstateType.HOUSE); //70%
        map.put(88.0, RealEstateType.BUILDING); //18%
        map.put(93.0, RealEstateType.COTTAGE); //5%
        map.put(100.0, RealEstateType.COMMERCE_SPACE); //7%
        double rand = random.nextDouble() * 100;
        return map.ceilingEntry(rand).getValue();
    }

    private static Long getBuildingStories(){
        TreeMap<Double, int[]> map = new TreeMap<>();
        Random random = new Random();

        map.put(65.0, new int[]{2, 10}); //65%
        map.put(92.5, new int[]{11, 20}); //27.5%
        map.put(97.5, new int[]{21, 30}); //5%
        map.put(100.0, new int[]{31, 40}); //2.5%

        double rand = random.nextDouble() * 100;

        int[] range = map.ceilingEntry(rand).getValue();

        return (long)(random.nextInt(range[1] - range[0] + 1) + range[0]);
    }



}