package com.example.nvt.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class RabbitMQListener {

    private final AtomicLong lastHeartbeatTimestamp = new AtomicLong();

    @RabbitListener(queues = "neki_queue")
    public void listen(String message) {
        // Update the timestamp of the last received heartbeat
        long currentTimestamp = System.currentTimeMillis();
        lastHeartbeatTimestamp.set(currentTimestamp);

        // Print the received message to the console
        System.out.println("Received heartbeat at " + currentTimestamp + ": " + message);
    }

    public long getLastHeartbeatTimestamp() {
        return lastHeartbeatTimestamp.get();
    }
}