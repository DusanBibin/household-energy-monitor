package com.example.nvt.helpers;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.example.nvt.configuration.ElasticsearchIndexConfig;
import com.example.nvt.configuration.InfluxProperties;
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
import com.influxdb.Cancellable;
import com.influxdb.client.*;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.client.write.events.WriteErrorEvent;
import com.influxdb.client.write.events.WriteSuccessEvent;
import com.influxdb.query.FluxRecord;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.sql.DataSource;
import java.io.*;
import java.sql.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.influxdb.client.domain.TemplateKind.BUCKET;
import static java.lang.Long.parseLong;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final int BATCH_SIZE = 5000;



    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final HouseholdRepository householdRepository;
    private final ClientRepository clientRepository;
    private final RealestateRepository realestateRepository;
    private final CityRepository cityRepository;
    private final RegionRepository regionRepository;
    private final MunicipalityRepository municipalityRepository;


    private final ClerkRepository clerkRepository;
    private final RealestateDocRepository realestateDocRepository;
    private final RegionDocRepository regionDocRepository;
    private final MunicipalityDocRepository municipalityDocRepository;
    private final CityDocRepository cityDocRepository;
    private final ElasticsearchClient esClient;

    private final InfluxDBClient influxDBClient;
    private final InfluxProperties influxProperties;

    private StopWatch stopWatch = new StopWatch();

    private Map<Long, String> regionDocMap = new HashMap<>();
    private Map<Long, String> municipalityDocMap = new HashMap<>();
    private Map<Long, String> cityDocMap = new HashMap<>();

    String passwordUniversal;

    Long clientCounter = 3L;

    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;



    // Base consumption values in kWh (for a typical household)
    private static final double BASE_NIGHT_CONSUMPTION = 0.1;
    private static final double BASE_DAY_CONSUMPTION = 0.3;
    private static final double PEAK_HOUR_MULTIPLIER = 1.3;

    // Seasonal multipliers
    private static final double WINTER_MULTIPLIER = 1.3;
    private static final double SUMMER_MULTIPLIER = 1.4;
    private static final double SPRING_MULTIPLIER = 0.9;
    private static final double FALL_MULTIPLIER = 0.8;

    // Appliance usage probabilities
    private static final double WEEKDAY_DAY_PROBABILITY = 0.7;
    private static final double WEEKEND_DAY_PROBABILITY = 0.9;
    private static final double WEEKDAY_NIGHT_PROBABILITY = 0.3;
    private static final double WEEKEND_NIGHT_PROBABILITY = 0.5;

    // Smaller random appliance spikes (0–2 kWh instead of 0–3 kWh)
    private static final double MAX_RANDOM_USAGE = 0.5;

    private final ElasticsearchIndexConfig elasticsearchIndexConfig;
    private final ApplicationArguments args;

    private String realestatesFile;
    private String householdsFile;
    private int clientNumber;
    @Override
    //@Transactional
    public void run(String... args) throws Exception {

        passwordUniversal = passwordEncoder.encode("sifra123");
        //initCitiesMunicipalitiesRegions();


        if (this.args.containsOption("fullData")) {
            realestatesFile = "REALESTATES.csv";
            householdsFile = "HOUSEHOLDS.csv";
            clientNumber = 7467936;
        } else {
            realestatesFile = "REALESTATES_LITE.csv";
            householdsFile = "HOUSEHOLDS_LITE.csv";
            clientNumber = 15921;
        }

        if (this.args.containsOption("initMode")) {


            boolean exists = false;
            while (!exists) {
                try {
                    BooleanResponse response = esClient.indices().exists(e -> e.index("realestate"));
                    if (response.value()) {
                        exists = true;
                        System.out.println("Index 'realestate' exists! Continuing...");
                    } else {
                        System.out.println("Index 'realestate' not found. Retrying in 2s...");
                        Thread.sleep(2000);
                    }
                } catch (Exception e) {
                    System.out.println("Error checking index existence. Retrying in 2s...");
                    Thread.sleep(2000);
                }
            }


            initializeData();
        } else {
            System.out.println("No init mode provided — skipping initialization");
        }


//        initializeData();
//        generateHistoricalData()
//        generateHistoricalDataToCsv();
//        generateHistoricalDataToLp();

    }

    protected void initializeData(){

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
                .password(passwordUniversal)
                .profileImg("dijamantmann.jpg")
                .emailConfirmed(true)
                .role(Role.ADMIN).build();
        userRepository.save(admin1);


        Client client1 = Client.builder()
                .email("dusanbibin2+client1@gmail.com")
                .firstName("Ime")
                .lastname("Prezime")
                .phoneNumber("0691817839")
                .password(passwordUniversal)
                .profileImg("dijamantmann.jpg")
                .emailConfirmed(true)
                .role(Role.CLIENT)
                .build();
        client1 = clientRepository.save(client1);

        Client client2 = Client.builder()
                .email("dusanbibin2+client2@gmail.com")
                .firstName("Ime")
                .lastname("Prezime")
                .phoneNumber("0697817839")
                .password(passwordUniversal)
                .profileImg("NEMA")
                .emailConfirmed(true)
                .role(Role.CLIENT)
                .build();
        client2 = clientRepository.save(client2);

        //initRealestates();
        //initClients();


        Map<Long, RegionDoc> regionDocMap = new HashMap<>();
        Map<Long, MunicipalityDoc> municipalityDocMap = new HashMap<>();
        Map<Long, CityDoc> cityDocMap = new HashMap<>();

        Map<Long, Long> cityMunicipality = new HashMap<>();
        Map<Long, Long> municipalityRegion = new HashMap<>();


        stopWatch.start("Initializing cities, municipalities, regions");
        System.out.println("Initializing cities, municipalities, regions...");

        String filePath = "/docker-entrypoint-initdb.d/REGIONS.csv";
        String sql = "COPY region(id, name) FROM '" + filePath + "' DELIMITER ',' CSV";
        jdbcTemplate.execute(sql);
        String seqName = jdbcTemplate.queryForObject("SELECT pg_get_serial_sequence('region', 'id')", String.class);
        jdbcTemplate.execute("SELECT setval('" + seqName + "', (SELECT MAX(id) FROM region))");

        filePath = "/docker-entrypoint-initdb.d/MUNICIPALITIES.csv";
        sql = "COPY municipality(id, name, region_id) FROM '" + filePath + "' DELIMITER ',' CSV";
        jdbcTemplate.execute(sql);
        seqName = jdbcTemplate.queryForObject("SELECT pg_get_serial_sequence('municipality', 'id')", String.class);
        jdbcTemplate.execute("SELECT setval('" + seqName + "', (SELECT MAX(id) FROM municipality))");

        filePath = "/docker-entrypoint-initdb.d/CITIES.csv";
        sql = "COPY city(id, name, zip_code, municipality_id) FROM '" + filePath + "' DELIMITER ',' CSV";
        jdbcTemplate.execute(sql);
        seqName = jdbcTemplate.queryForObject("SELECT pg_get_serial_sequence('city', 'id')", String.class);
        jdbcTemplate.execute("SELECT setval('" + seqName + "', (SELECT MAX(id) FROM city))");

        Region region = Region.builder()
                .name("kurac").build();

        regionRepository.save(region);

        stopWatch.stop();
        System.out.println("DONE");


        stopWatch.start("Initializing clients");
        System.out.println("Initializing clients...");
        //lite 15921
        //regular 7467936
        List<Client> clients = new ArrayList<>();
        for(int i = 0; i < clientNumber; i++) {
            Client client = createClient();
            clients.add(client);

            if(clients.size() >= BATCH_SIZE) {
//                clientRepository.saveAll(clients);
                System.out.println(i + 1);
                insertClients(clients);
                clients.clear();
            }
        }

        if(!clients.isEmpty()){
//            clientRepository.saveAll(clients);
            System.out.println("zadnjii");
            insertClients(clients);
            clients.clear();
        }





        stopWatch.stop();
        System.out.println("DONE");

        stopWatch.start("Initializing realestates");
        System.out.println("Initializing realestates...");


        filePath = "/docker-entrypoint-initdb.d/" + realestatesFile;
        sql = "COPY realestate(id, city_id, lat, lon, address_street, address_num, type, realestate_owner_id, total_floors, apartment_per_floor_num, is_vacant) FROM '" + filePath + "' DELIMITER ',' CSV";
        jdbcTemplate.execute(sql);
        seqName = jdbcTemplate.queryForObject("SELECT pg_get_serial_sequence('realestate', 'id')", String.class);
        jdbcTemplate.execute("SELECT setval('" + seqName + "', (SELECT MAX(id) FROM realestate))");

        stopWatch.stop();
        System.out.println("DONE");

        stopWatch.start("Initializing region index");
        System.out.println("Initializing region index...");

        //BulkRequest.Builder breq = new BulkRequest.Builder();

        List<RegionDoc> regions = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/REGIONS.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 2);

                RegionDoc doc = RegionDoc.builder()
                        .dbId(Long.parseLong(parts[0]))
                        .region(parts[1])
                        .build();

                regions.add(doc);

                if(regions.size() >= BATCH_SIZE){
//                    for(RegionDoc r: regions){
//                        breq.operations(op -> op
//                                .index(idx -> idx
//                                        .index("region")
//                                        .document(r)
//                                )
//                        );
//                    }
                    Iterable<RegionDoc> regionDocsIter = regionDocRepository.saveAll(regions);

                    for(RegionDoc regionDoc : regionDocsIter){
                        regionDocMap.put(regionDoc.getDbId(), regionDoc);
                    }

                    regions.clear();
                }
            }

            if(!regions.isEmpty()){
//                for(RegionDoc r: regions){
//                    breq.operations(op -> op
//                            .index(idx -> idx
//                                    .index("region")
//                                    .document(r)
//                            )
//                    );
//                }
                Iterable<RegionDoc> regionDocsIter = regionDocRepository.saveAll(regions);

                for(RegionDoc regionDoc : regionDocsIter){
                    regionDocMap.put(regionDoc.getDbId(), regionDoc);
                }
                regions.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            BulkResponse result = esClient.bulk(breq.build());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        stopWatch.stop();
        System.out.println("DONE");

        stopWatch.start("Initializing municipality index");
        System.out.println("Initializing municipality index...");

        //breq = new BulkRequest.Builder();
        List<MunicipalityDoc> municipalities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/MUNICIPALITIES.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 3);

                Long regionId = Long.parseLong(parts[2]);
                Long municipalityId = Long.parseLong(parts[0]);
                municipalityRegion.put(municipalityId, regionId);

                String regionName = regionDocMap.get(regionId).getRegion();

                MunicipalityDoc doc = MunicipalityDoc.builder()
                        .dbId(municipalityId)
                        .municipality(parts[1] + ", " + regionName)
                        .build();

                municipalities.add(doc);

                if(municipalities.size() >= BATCH_SIZE){
//                    for(MunicipalityDoc m: municipalities){
//                        breq.operations(op -> op
//                                .index(idx -> idx
//                                        .index("municipality")
//                                        .document(m)
//                                )
//                        );
//                    }
                    Iterable<MunicipalityDoc> municipalityDocsIter = municipalityDocRepository.saveAll(municipalities);


                    for(MunicipalityDoc municipalityDoc : municipalityDocsIter){
                        municipalityDocMap.put(municipalityDoc.getDbId(), municipalityDoc);
                    }
                    municipalities.clear();
                }
            }

            if(!municipalities.isEmpty()){
//                for(MunicipalityDoc m: municipalities){
//                    breq.operations(op -> op
//                            .index(idx -> idx
//                                    .index("municipality")
//                                    .document(m)
//                            )
//                    );
//                }


                Iterable<MunicipalityDoc> municipalityDocsIter = municipalityDocRepository.saveAll(municipalities);

                for(MunicipalityDoc municipalityDoc : municipalityDocsIter){
                    municipalityDocMap.put(municipalityDoc.getDbId(), municipalityDoc);
                }
                municipalities.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            BulkResponse result = esClient.bulk(breq.build());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }



        stopWatch.stop();
        System.out.println("DONE");

        stopWatch.start("Initializing city index");
        System.out.println("Initializing city index...");

//        breq = new BulkRequest.Builder();
        List<CityDoc> cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/CITIES.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 4);

                Long cityId = Long.parseLong(parts[0]);
                Long municipalityId = Long.parseLong(parts[3]);
                cityMunicipality.put(cityId, municipalityId);

                String municipalityName = municipalityDocMap.get(municipalityId).getMunicipality();

                CityDoc doc = CityDoc.builder()
                        .dbId(Long.parseLong(parts[0]))
                        .city(parts[1] + ", " + municipalityName)
                        .zipCode(parts[2])
                        .build();

                cities.add(doc);

                if(cities.size() >= BATCH_SIZE){
//                    for(CityDoc c: cities){
//                        breq.operations(op -> op
//                                .index(idx -> idx
//                                        .index("city")
//                                        .document(c)
//                                )
//                        );
//                    }
                    Iterable<CityDoc> cityDocsIter = cityDocRepository.saveAll(cities);

                    for(CityDoc cityDoc : cityDocsIter){
                        cityDocMap.put(cityDoc.getDbId(), cityDoc);
                    }
                    cities.clear();
                }
            }

            if(!cities.isEmpty()){
//                for(CityDoc c: cities){
//                    breq.operations(op -> op
//                            .index(idx -> idx
//                                    .index("city")
//                                    .document(c)
//                            )
//                    );
//                }
                Iterable<CityDoc> cityDocsIter = cityDocRepository.saveAll(cities);

                for(CityDoc cityDoc : cityDocsIter){
                    cityDocMap.put(cityDoc.getDbId(), cityDoc);
                }
                cities.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            BulkResponse result = esClient.bulk(breq.build());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }




        stopWatch.stop();
        System.out.println("DONE");

        stopWatch.start("Initializing realestates index");
        System.out.println("Initializing realestates index...");

        List<RealestateDoc> realestates = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("data/" + realestatesFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", 11);

                Long realestateId = Long.parseLong(parts[0]);
                Long cityId = Long.parseLong(parts[1]);
                Double lat = Double.parseDouble(parts[2]);
                Double lon = Double.parseDouble(parts[3]);
                String addressStreet = parts[4];
                String addressNumber = parts[5];
                RealEstateType type = null;
                try{
                    type = RealEstateType.values()[Integer.parseInt(parts[6])];
                }catch(ArrayIndexOutOfBoundsException e){
                    System.out.println("Invalid RealEstateType: " + line);
                    e.printStackTrace();
                }

                //Long ownerId = Long.parseLong(parts[7]);
                Long totalFloors = Long.parseLong(parts[8]);
                //Long apartmentsPerFloor = Long.parseLong(parts[9]);
                Boolean isVacant = Boolean.valueOf(parts[10]);

                CityDoc cd = cityDocMap.get(cityId);
                MunicipalityDoc md = municipalityDocMap.get(cityMunicipality.get(cityId));
                RegionDoc rd = regionDocMap.get(municipalityRegion.get(md.getDbId()));

                String fullAddress = addressStreet + " " + addressNumber + " " + cd.getZipCode()
                        + " " + cd.getCity();
//                        + " " + md.getMunicipality() + " " + rd.getRegion();


                RealestateDoc doc = RealestateDoc.builder()
                        .dbId(realestateId)
                        .address(fullAddress)
                        .type(type)
                        .cityDocId(cd.getId())
                        .municipalityDocId(md.getId())
                        .regionDocId(rd.getId())
                        .location(lat + "," + lon)
                        .vacant(isVacant)
                        .build();

                realestates.add(doc);

                if(realestates.size() >= BATCH_SIZE){
                    Iterable<RealestateDoc> realestateDocsIter = realestateDocRepository.saveAll(realestates);
                    System.out.println("ubacujemo realestateove");
                    realestates.clear();
                }
            }

            if(!realestates.isEmpty()){
                Iterable<RealestateDoc> realestateDocsIter = realestateDocRepository.saveAll(realestates);
                System.out.println("zavrsili smo");
                realestates.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        try {
//            BulkResponse result = esClient.bulk(breq.build());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }




        stopWatch.stop();
        System.out.println("DONE");










        stopWatch.start("Initializing households");
        System.out.println("Initializing households...");

        filePath = "/docker-entrypoint-initdb.d/" + householdsFile;
        sql = "COPY household(realestate_id, household_owner_id, apartment_num, size) FROM '" + filePath + "' DELIMITER ',' CSV";
        jdbcTemplate.execute(sql);
//

        stopWatch.stop();
        System.out.println("DONE");





        stopWatch.start("Initializing influxDb data");
        System.out.println("Initializing influxDb data...");


//        OVDE IDE INFLUXDB


        stopWatch.stop();
        System.out.println("DONE");





        stopWatch.start("Initializing clerks");
        System.out.println("Initializing clerks...");



        Clerk clerk1 = Clerk.builder()
                .email("dusanbibin2+clerk1@gmail.com")
                .firstName("Clerk1")
                .lastname("Prezime")
                .phoneNumber("0691817839")
                .password(passwordUniversal)
                .verification(new Verification())
                .profileImg("NEMA")
                .emailConfirmed(true)
                .appointments(new ArrayList<>())
                .role(Role.CLERK)
                .build();
        clerk1 = clerkRepository.save(clerk1);

        Clerk clerk2 = Clerk.builder()
                .email("dusanbibin2+clerk2@gmail.com")
                .firstName("Clerk2")
                .lastname("Prezime")
                .phoneNumber("0691817839")
                .password(passwordUniversal)
                .verification(new Verification())
                .profileImg("NEMA")
                .emailConfirmed(true)
                .appointments(new ArrayList<>())
                .role(Role.CLERK)
                .build();
        clerk2 = clerkRepository.save(clerk2);

        Clerk clerk3 = Clerk.builder()
                .email("dusanbibin2+clerk3@gmail.com")
                .firstName("Clerk3")
                .lastname("Prezime")
                .phoneNumber("0691817839")
                .password(passwordUniversal)
                .verification(new Verification())
                .profileImg("NEMA")
                .emailConfirmed(true)
                .appointments(new ArrayList<>())
                .role(Role.CLERK)
                .build();
        clerk3 = clerkRepository.save(clerk3);



        stopWatch.stop();
        System.out.println("DONE");


        System.out.println(stopWatch.prettyPrint());
    }


    public void insertClients(List<Client> clients) {
        String userSql = "INSERT INTO _user (email, first_name, lastname, phone_number, password, email_confirmed, profile_img, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String clientSql = "INSERT INTO client (id) VALUES (?)"; // `id` from _user table

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false); // manage transaction manually

            try (
                    PreparedStatement userStmt = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                    PreparedStatement clientStmt = connection.prepareStatement(clientSql)
            ) {
                for (Client client : clients) {
                    userStmt.setString(1, client.getEmail());
                    userStmt.setString(2, client.getFirstName());
                    userStmt.setString(3, client.getLastname());
                    userStmt.setString(4, client.getPhoneNumber());
                    userStmt.setString(5, client.getPassword());
                    userStmt.setBoolean(6, client.isEmailConfirmed());
                    userStmt.setString(7, client.getProfileImg());
                    userStmt.setString(8, client.getRole().name());
                    userStmt.executeUpdate();

                    ResultSet keys = userStmt.getGeneratedKeys();
                    if (keys.next()) {
                        long userId = keys.getLong("id");
                        clientStmt.setLong(1, userId);
                        clientStmt.addBatch();
                    }
                }

                clientStmt.executeBatch();
                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //@Transactional
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
                Long zipCode = parseLong(line[1]);
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

                Long counter = 0L;
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


    //@Transactional
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
        Long counterNum = 0L;
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


                City city = cityMap.get(cityName + "_Opština " + municipalityName);
                if (city == null) {
                    System.out.println("City not found: " + cityName + ", " + municipalityName);
                    continue;
                }

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
                        //Client clientH = createClient();

                        Household household = Household.builder()
                                .size(random.nextDouble(71) + 40)
                                .authorizedViewers(new ArrayList<>())
                                //.householdOwner(clientH)
                                .realestate(realestate)
                                .build();

                        //realestate.setVacant(clientH == null);
                        realestate.setTotalFloors(random.nextLong(1) + 3);
                        realestate.getHouseholds().add(household);
                    }
                    case COTTAGE -> {
                        //Client clientH = createClient();

                        Household household = Household.builder()
                                .size(random.nextDouble(100) + 100)
                                .authorizedViewers(new ArrayList<>())
                                //.householdOwner(clientH)
                                .realestate(realestate)
                                .build();


                        //realestate.setVacant(clientH == null);
                        realestate.setTotalFloors(1L);
                        realestate.getHouseholds().add(household);
                    }
                    case BUILDING -> {
                        Long totalFloors = getBuildingStories();
                        Long apartmentsPerFloor = random.nextLong(20) + 2;
                        Double apartmentSize = random.nextDouble(60) + 10;

                        Long counter = 0L;
                        boolean isVacant = true;
                        for(int i = 0; i < totalFloors; i++) {
                            for (int j = 0; j < apartmentsPerFloor; j++) {
                                counter++;

                                //Client clientH = createClient();

                                Household household = Household.builder()
                                        .size(apartmentSize)
                                        .apartmentNum(counter)
                                        .authorizedViewers(new ArrayList<>())
                                        //.householdOwner(clientH)
                                        .realestate(realestate)
                                        .build();

//                                if(clientH == null && !isVacant){
//                                    isVacant = true;
//                                }


                                realestate.getHouseholds().add(household);

                            }
                        }

                        realestate.setVacant(isVacant);
                        realestate.setTotalFloors(totalFloors);
                        realestate.setApartmentPerFloorNum(apartmentsPerFloor);

                    }
                    case COMMERCE_SPACE -> {
                        //Client clientH = createClient();

                        Household household = Household.builder()
                                .size(random.nextDouble(230) + 20)
                                .authorizedViewers(new ArrayList<>())
                                .realestate(realestate)
                                //.householdOwner(clientH)
                                .build();


                        //realestate.setVacant(clientH == null);
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
                                                    .vacant(r.isVacant())
                                                    .build(); })
                                    .toList();

                    realestateDocRepository.saveAll(realestateDocs);
                    realestates.clear();

                    System.out.println("Batch num " + ++counterNum);
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

                        String zipCode = (c.getZipCode() == 0L)? " " : " " + c.getZipCode() + " ";

                        String fullAddress = r.getAddressStreet() + " " + r.getAddressNum() + zipCode + c.getName()
                                + " " + m.getName() + " " + reg.getName();
                        return RealestateDoc.builder()
                                .dbId(r.getId())
                                .address(fullAddress)
                                .type(r.getType())
                                .cityDocId(cityDocMap.get(c.getId()))
                                .municipalityDocId(municipalityDocMap.get(m.getId()))
                                .regionDocId(regionDocMap.get(reg.getId()))
                                .location(r.getLat() + "," + r.getLon())
                                .vacant(r.isVacant())
                                .build(); })
                    .toList();
            realestateDocRepository.saveAll(realestateDocs);
            realestates.clear();

            System.out.println("Last batch num " + ++counterNum);
        }
        stopWatch.stop();
        System.out.println("DONE in " + stopWatch.getTotalTimeSeconds() + " seconds");

    }


    private Client createClient(){

        Client client = null;
//        if(random.nextLong(100L) >= 10){
            client = Client.builder()
                    .email("dusanbibin2+" + "client" + clientCounter+ "@gmail.com")
                    .firstName("Ime" + clientCounter)
                    .lastname("Prezime" + clientCounter)
                    .phoneNumber("0697817839")
                    .password(passwordUniversal)
                    .profileImg("NEMA")
                    .emailConfirmed(true)
                    .realEstates(new ArrayList<>())
                    .assetRequests(new ArrayList<>())
                    .households(new ArrayList<>())
                    .role(Role.CLIENT)
                    .build();
            clientCounter++;
//        }
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

    public void generateHistoricalDataToCsv() {
        String csvFilePath = "electricity_compact.csv";
        List<Long> householdIds = householdRepository.getAllOwnedHouseholds(PageRequest.of(0, 1000));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {

            writer.write("id,kWh,_time\n");

            LocalDateTime start = LocalDateTime.now().minusYears(3);
            LocalDateTime end = LocalDateTime.now();

            for (Long householdId : householdIds) {
                LocalDateTime current = start.truncatedTo(ChronoUnit.HOURS);

                while (!current.isAfter(end)) {
                    double consumption = generateHourlyConsumption(current);

                    // Write compact CSV line
                    writer.write(String.format(
                            "%d,%.3f,%s\n",  // Removed measurement from each row
                            householdId,
                            consumption,
                            current.toInstant(ZoneOffset.UTC)
                    ));

                    current = current.plusHours(1);
                }
                if (householdId % 100 == 0) {
                    System.out.printf("Progress: %d/%d households\n",
                            householdIds.indexOf(householdId)+1,
                            householdIds.size());
                }
            }
            System.out.println("Compact CSV generated: " + csvFilePath);
            System.out.printf("Total rows: ~%,d\n",
                    householdIds.size() * ChronoUnit.HOURS.between(start, end));
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }



    public void generateHistoricalData() {
        List<Long> occupiedHouseholdIds = householdRepository.getAllOwnedHouseholds(PageRequest.of(0, 1000));

        try (WriteApi writeApi = influxDBClient.getWriteApi()) {

            LocalDateTime start = LocalDateTime.now().minusYears(3);
            LocalDateTime end = LocalDateTime.now();

            for (Long householdId : occupiedHouseholdIds) {
                LocalDateTime current = start.truncatedTo(ChronoUnit.HOURS);

                while (!current.isAfter(end)) {
                    double consumption = generateHourlyConsumption(current);

                    Point point = Point
                            .measurement("E")
                            .addTag("hId", householdId.toString())
                            .addField("kWh", consumption)
                            .time(current.toInstant(ZoneOffset.UTC), WritePrecision.NS);

                    writeApi.writePoint(influxProperties.getBucket(), influxProperties.getOrg(), point);

                    current = current.plusHours(1);
                }
                System.out.println("Finished backfilling household " + householdId);
            }


        }
    }

    public void generateHistoricalDataToLp() {
        String lpFilePath = "electricity_compact.lp";
        List<Long> householdIds = householdRepository.getAllOwnedHouseholds(PageRequest.of(0, 1000));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(lpFilePath))) {

            LocalDateTime start = LocalDateTime.now().minusYears(3);
            LocalDateTime end = LocalDateTime.now();

            for (Long householdId : householdIds) {
                LocalDateTime current = start.truncatedTo(ChronoUnit.HOURS);

                while (!current.isAfter(end)) {
                    double consumption = generateHourlyConsumption(current);

                    // Convert LocalDateTime -> nanoseconds since epoch (UTC)
                    long tsNano = current.toInstant(ZoneOffset.UTC).toEpochMilli() * 1_000_000;

                    // Write InfluxDB line protocol
                    // Example: E,hId=123 kWh=4.567 1672531200000000000
                    String lpLine = String.format(
                            "E,hId=%d kWh=%.3f %d",
                            householdId,
                            consumption,
                            tsNano
                    );
                    writer.write(lpLine);
                    writer.newLine();

                    current = current.plusHours(1);
                }

                if (householdId % 100 == 0) {
                    System.out.printf("Progress: %d/%d households\n",
                            householdIds.indexOf(householdId) + 1,
                            householdIds.size());
                }
            }

            System.out.println("Line Protocol file generated: " + lpFilePath);
            System.out.printf("Total rows: ~%,d\n",
                    householdIds.size() * ChronoUnit.HOURS.between(start, end));
        } catch (IOException e) {
            System.err.println("Error writing LP: " + e.getMessage());
        }
    }




    public static double generateHourlyConsumption(LocalDateTime dateTime) {
        int hour = dateTime.getHour();
        boolean isWeekend = dateTime.getDayOfWeek().getValue() >= 6;
        boolean isDaytime = isDaytime(hour, dateTime.getMonthValue());

        double consumption = isDaytime ? BASE_DAY_CONSUMPTION : BASE_NIGHT_CONSUMPTION;

        // Peak hours (7–9 AM, 5–9 PM)
        if ((hour >= 7 && hour <= 9) || (hour >= 17 && hour <= 21)) {
            consumption *= PEAK_HOUR_MULTIPLIER;
        }

        // Seasonal adjustment
        consumption *= getSeasonalMultiplier(dateTime.getMonthValue());

        // Random usage (scaled by probability)
        double usageProbability = isWeekend ?
                (isDaytime ? WEEKEND_DAY_PROBABILITY : WEEKEND_NIGHT_PROBABILITY) :
                (isDaytime ? WEEKDAY_DAY_PROBABILITY : WEEKDAY_NIGHT_PROBABILITY);

        double randomUsage = Math.random() * MAX_RANDOM_USAGE * usageProbability;
        consumption += randomUsage;

        // Ensure it never goes below a realistic minimum (e.g., fridge)
        return Math.max(consumption, 0.05); // 0.05 kWh = 50W (absolute minimum)
    }

    private static boolean isDaytime(int hour, int month) {
        // Simplified day/night calculation based on season
        int sunrise, sunset;

        if (month >= 11 || month <= 2) { // Winter
            sunrise = 7;
            sunset = 17;
        } else if (month >= 3 && month <= 5) { // Spring
            sunrise = 6;
            sunset = 19;
        } else if (month >= 6 && month <= 8) { // Summer
            sunrise = 5;
            sunset = 21;
        } else { // Fall
            sunrise = 6;
            sunset = 18;
        }

        return hour >= sunrise && hour < sunset;
    }

    private static double getSeasonalMultiplier(int month) {
        if (month >= 11 || month <= 2) { // Winter (Nov-Feb)
            return WINTER_MULTIPLIER;
        } else if (month >= 3 && month <= 5) { // Spring (Mar-May)
            return SPRING_MULTIPLIER;
        } else if (month >= 6 && month <= 8) { // Summer (Jun-Aug)
            return SUMMER_MULTIPLIER;
        } else { // Fall (Sep-Oct)
            return FALL_MULTIPLIER;
        }
    }




}