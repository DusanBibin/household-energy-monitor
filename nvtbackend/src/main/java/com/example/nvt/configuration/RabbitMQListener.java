package com.example.nvt.configuration;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQListener {

    @RabbitListener(queues = "my-queue")
    public void listen(String message) {
        System.out.println("Received message: " + message);
    }
}