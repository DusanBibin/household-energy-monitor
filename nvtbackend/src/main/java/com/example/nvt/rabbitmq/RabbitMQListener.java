package com.example.nvt.rabbitmq;

import com.example.nvt.DTO.ConsumptionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Component
public class RabbitMQListener {

    private final AtomicLong lastHeartbeatTimestamp = new AtomicLong();
    private final InfluxDBClient influxDBClient;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "neki_queue")
    public void listenHeartBeat(String message) {

        long currentTimestamp = System.currentTimeMillis();
        lastHeartbeatTimestamp.set(currentTimestamp);


        System.out.println("Received heartbeat at " + currentTimestamp + ": " + message);
    }


    @RabbitListener(queues = "values")
    public void listenValues(String message) {
        try {
            // Parse incoming JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);

            String householdId = jsonNode.get("household_id").asText();


            long currentTimestamp = System.currentTimeMillis();
            lastHeartbeatTimestamp.set(currentTimestamp);





            String timestamp = jsonNode.get("timestamp").asText();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    .withZone(ZoneOffset.UTC);

            Instant instant;
            try {

                long epochSeconds = Long.parseLong(timestamp);
                instant = Instant.ofEpochSecond(epochSeconds);
            } catch (NumberFormatException e) {

                LocalDateTime localDateTime = LocalDateTime.parse(timestamp, formatter);
                instant = localDateTime.toInstant(ZoneOffset.UTC);
            }

            String formattedTimestamp = DateTimeFormatter.ISO_INSTANT.format(instant);
            double consumption = jsonNode.get("consumption").asDouble();




            ConsumptionDTO consumptionDTO = ConsumptionDTO.builder()
                    .kWh(consumption)
                    .datetime(formattedTimestamp).build();
            System.out.println(consumptionDTO);
            messagingTemplate.convertAndSend("/consumption-realtime/" + householdId, consumptionDTO);


        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse message: " + message);
            e.printStackTrace();
        }
    }
    //                try (WriteApi writeApi = influxDBClient.getWriteApi()) {
//                    Point point = Point.measurement("electricity_consumption")
//                            .addTag("householdId", householdId)
//                            .addField("kWh", consumption)
//                            .time(timestampMillis, WritePrecision.MS);
//                    writeApi.writePoint("nvt", "nvt", point);
//                    writeApi.flush();
//                }
    public long getLastHeartbeatTimestamp() {
        return lastHeartbeatTimestamp.get();
    }
}