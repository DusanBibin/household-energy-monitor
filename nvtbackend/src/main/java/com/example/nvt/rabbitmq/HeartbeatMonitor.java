package com.example.nvt.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HeartbeatMonitor {

    @Autowired
    private RabbitMQListener rabbitMQListener;

    // Check if the heartbeat has been received in the last 30 seconds
    @Scheduled(fixedRate = 30000) // Runs every 30 seconds
    public void checkHeartbeat() {
        System.out.println("Checking heartbeat...");
        long lastHeartbeatTimestamp = rabbitMQListener.getLastHeartbeatTimestamp();
        long currentTimestamp = System.currentTimeMillis();

        // If more than 30 seconds have passed since the last heartbeat, log a message
        if (TimeUnit.MILLISECONDS.toSeconds(currentTimestamp - lastHeartbeatTimestamp) >= 30) {
            System.out.println("No heartbeat received in the last 30 seconds.");
        } else {
            System.out.println("Heartbeat received within the last 30 seconds.");
        }

    }
}