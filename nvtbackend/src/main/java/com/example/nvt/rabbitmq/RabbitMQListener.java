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
import java.time.ZoneId;
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

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(message);

            String householdId = jsonNode.get("household_id").asText();
            String timestamp = jsonNode.get("timestamp").asText(); // e.g. "2025-08-09T14:30:00"
            double consumption = jsonNode.get("consumption").asDouble();

            System.out.println(householdId);
            System.out.println(timestamp);
            System.out.println(consumption);

            LocalDateTime localDateTime = LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            ZoneId zoneId = ZoneId.systemDefault();
            Instant instant = localDateTime.atZone(zoneId).toInstant();

            try (WriteApi writeApi = influxDBClient.getWriteApi()) {
                Point point = Point.measurement("E")
                        .addTag("hId", householdId)
                        .addField("kWh", consumption)
                        .time(instant, WritePrecision.NS);
                writeApi.writePoint("nvt", "nvt", point);
                writeApi.flush();
            }




            ConsumptionDTO consumptionDTO = ConsumptionDTO.builder()
                    .kWh(consumption)
                    .datetime(localDateTime.toString()).build();
            System.out.println(consumptionDTO);
            messagingTemplate.convertAndSend("/consumption-realtime/" + householdId, consumptionDTO);


        } catch (Exception e) {
            System.err.println("[ERROR] Failed to parse message: " + message);
            e.printStackTrace();
        }
    }

    public long getLastHeartbeatTimestamp() {
        return lastHeartbeatTimestamp.get();
    }
}