package com.example.nvt.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    @Bean
    public Queue queueHeartbeats() {
        return new Queue("neki_queue", true); // 'true' makes the queue durable
    }

    @Bean
    public Queue queueValues() {return new Queue("values", true);}

}