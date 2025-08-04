package com.example.nvt.controller;


import com.example.nvt.DTO.ConsumptionDTO;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConsumptionQueryController {

    private final InfluxDBClient influxDBClient;
    private static String BUCKET = "nvt";
    private static String ORG = "nvt";



    @GetMapping("/monthly")
    public ResponseEntity<List<ConsumptionDTO>> getMonthly(
            @RequestParam int householdId,
            @RequestParam(required = false) Integer startYear,
            @RequestParam(required = false) Integer startMonth) {


        YearMonth start = (startYear == null || startMonth == null)
                ? YearMonth.now()
                : YearMonth.of(startYear, startMonth);


        YearMonth end = start.plusMonths(1);
        YearMonth begin = start.minusMonths(11);

        ZonedDateTime from = begin.atDay(1).atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime to   = end.atDay(1).atStartOfDay(ZoneId.systemDefault());

        String flux = String.format("""
                from(bucket: "%s")
                  |> range(start: %s, stop: %s)
                  |> filter(fn:(r) => r._measurement == "electricity_consumption" and r.householdId == "%d" and r._field == "kWh")
                  |> aggregateWindow(every: 1mo, fn: sum)
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

            // round val to 2 decimal places
            double roundedVal = BigDecimal.valueOf(val)
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            result.add(new ConsumptionDTO(ym.toString(), roundedVal));
        }

        return ResponseEntity.ok(result);
    }


    @GetMapping("/daily")
    public ResponseEntity<List<ConsumptionDTO>> getDaily(
            @RequestParam int householdId,
            @RequestParam int year,
            @RequestParam int month) {

        YearMonth ym = YearMonth.of(year, month);
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth().plusDays(1);

        ZonedDateTime from = startDate.atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime to = endDate.atStartOfDay(ZoneId.systemDefault());

        String flux = String.format("""
            from(bucket: "%s")
              |> range(start: %s, stop: %s)
              |> filter(fn:(r) => r._measurement == "electricity_consumption" and r.householdId == "%d" and r._field == "kWh")
              |> aggregateWindow(every: 1d, fn: sum)
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

        return ResponseEntity.ok(result);
    }
}
