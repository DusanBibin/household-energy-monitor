package com.example.nvt.service;

import com.example.nvt.DTO.ConsumptionDTO;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.Client;
import com.example.nvt.model.Household;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ConsumptionQueryService {
    private final HouseholdService householdService;

    private final InfluxDBClient influxDBClient;
    private static String BUCKET = "nvt";
    private static String ORG = "nvt";

    public List<ConsumptionDTO> getYearlyConsumption(Client client, Long householdId, Integer startYear, Integer startMonth) {


        Household household = householdService.getHouseholdByIdAndClientId(client.getId(), householdId);
        YearMonth start = (startYear == null || startMonth == null)
                ? YearMonth.now()
                : YearMonth.of(startYear, startMonth);


        YearMonth end = start.plusMonths(1);
        YearMonth begin = start.minusMonths(11);



        ZonedDateTime from = begin.atDay(1).atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime to   = end.atDay(1).atStartOfDay(ZoneOffset.UTC);



        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn:(r) => r._measurement == "electricity_consumption" and r.householdId == "%d" and r._field == "kWh")
                  |> aggregateWindow(every: 1mo, fn: sum, createEmpty: true, timeSrc: "_start")
                  |> yield(name: "sum")
                """,
                BUCKET,
                from.toInstant(),
                to.toInstant(),
                householdId
        );

        QueryApi queryApi = influxDBClient.getQueryApi();

        List<FluxTable> tables = queryApi.query(flux, ORG);
        Map<YearMonth, Double> aggregated = new HashMap<>();

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant ts = record.getTime();
                YearMonth ym = YearMonth.from(ts.atZone(ZoneId.systemDefault()));
                Double value = record.getValueByKey("_value") != null ? ((Number) record.getValueByKey("_value")).doubleValue() : 0d;
                aggregated.put(ym, value);
            }
        }


        List<ConsumptionDTO> result = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            YearMonth ym = begin.plusMonths(i);
            Double val = aggregated.getOrDefault(ym, 0d);


            double roundedVal = BigDecimal.valueOf(val)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            result.add(new ConsumptionDTO(ym.toString(), roundedVal));
        }

        return result;
    }

    public List<ConsumptionDTO> getMonthlyConsumption(Client client, Long householdId, int year, int month) {

        Household household = householdService.getHouseholdByIdAndClientId(client.getId(), householdId);

        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth().plusDays(1);


        ZonedDateTime from = startDate.atStartOfDay(ZoneOffset.UTC);
        ZonedDateTime to = endDate.atStartOfDay(ZoneOffset.UTC);

        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: %s, stop: %s)
              |> filter(fn:(r) => r._measurement == "electricity_consumption" and r.householdId == "%d" and r._field == "kWh")
              |> aggregateWindow(every: 1d, fn: sum, createEmpty: true, timeSrc: "_start")
              |> yield(name: "sum")
            """,
                BUCKET,
                from.toInstant(),
                to.toInstant(),
                householdId
        );

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, ORG);

        Map<LocalDate, Double> aggregated = new HashMap<>();
        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant ts = record.getTime();
                LocalDate day = ts.atZone(ZoneId.systemDefault()).toLocalDate();
                Double value = record.getValueByKey("_value") != null
                        ? ((Number) record.getValueByKey("_value")).doubleValue()
                        : 0d;
                aggregated.put(day, value);
            }
        }

        List<ConsumptionDTO> result = new ArrayList<>();
        for (int i = 0; i < ym.lengthOfMonth(); i++) {
            LocalDate d = startDate.plusDays(i);
            double val = aggregated.getOrDefault(d, 0d);
            result.add(new ConsumptionDTO(d.toString(), val));
        }
        return result;
    }

    private Instant parsePeriodToInstant(String period, Instant reference) {
        try {
            if (period.endsWith("h")) {
                int hours = Integer.parseInt(period.replace("h", ""));
                return reference.minus(Duration.ofHours(hours));
            } else if (period.endsWith("d")) {
                int days = Integer.parseInt(period.replace("d", ""));
                return reference.minus(Duration.ofDays(days));
            } else if (period.endsWith("m")) {
                int months = Integer.parseInt(period.replace("m", ""));
                // Convert to ZonedDateTime in system default zone, subtract months, then back to Instant
                ZonedDateTime zdt = reference.atZone(ZoneId.systemDefault()).minusMonths(months);
                return zdt.toInstant();
            } else if (period.endsWith("y")) {
                int years = Integer.parseInt(period.replace("y", ""));
                ZonedDateTime zdt = reference.atZone(ZoneId.systemDefault()).minusYears(years);
                return zdt.toInstant();
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String chooseDownsampleInterval(Duration duration) {
        long days = duration.toDays();

        if (days <= 1) {
            return "1m";  // 1 minute buckets
        } else if (days <= 7) {
            return "10m"; // 10 minutes
        } else if (days <= 30) {
            return "1h";  // 1 hour
        } else if (days <= 90) {
            return "6h";  // 6 hours
        } else {
            return "1d";  // 1 day
        }
    }

    private String buildFluxQuery(Long householdId, Instant start, Instant end, String downsampleInterval) {
        return String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn: (r) => r._measurement == "electricity_consumption" and r.householdId == "%d" and r._field == "kWh")
                  |> aggregateWindow(every: %s, fn: mean, createEmpty: false)
                  |> yield(name: "mean")
                """,
                BUCKET,
                start.toString(),
                end.toString(),
                householdId,
                downsampleInterval
        );

    }

    public List<ConsumptionDTO> getConsumption(Long householdId, Client client, String period, Instant from, Instant to) {

        Household household = householdService.getHouseholdByIdAndClientId(client.getId(), householdId);

        if ((period == null || period.isEmpty()) && (from == null || to == null)) {
            throw new InvalidInputException("Parameters are mising");
        }

        Instant start;
        Instant end;

        // Calculate start & end based on input
        if (period != null && !period.isEmpty()) {
            end = Instant.now();
            start = parsePeriodToInstant(period, end);
            if (start == null) {
                throw new InvalidInputException("Start date is missing");
            }
        } else {
            // from and to must be provided
            if (to.isBefore(from)) {
                throw new InvalidInputException("Start date cannot be after end date");
            }
            // max 1 year difference
            if (Duration.between(from, to).toDays() > 365) {
                throw new InvalidInputException("Date range can only be up to 1 year");
            }
            start = from;
            end = to;
        }

        // Determine downsampling interval depending on range (simple heuristic)
        String downsampleInterval = chooseDownsampleInterval(Duration.between(start, end));

        String flux = buildFluxQuery(householdId, start, end, downsampleInterval);

        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(flux, ORG);

        List<ConsumptionDTO> result = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneOffset.UTC);

        for (FluxTable table : tables) {
            for (FluxRecord record : table.getRecords()) {
                Instant time = (Instant) record.getValueByKey("_time");
                Double kWh = ((Number) record.getValueByKey("_value")).doubleValue();

                result.add(new ConsumptionDTO(formatter.format(time), kWh));
            }
        }

        return result;
    }


}
