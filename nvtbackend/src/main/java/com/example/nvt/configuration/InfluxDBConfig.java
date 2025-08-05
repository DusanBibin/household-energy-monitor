package com.example.nvt.configuration;


import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfluxDBConfig {
    @Bean
    public InfluxDBClient customInfluxDBConfig() {
        String url = "http://localhost:8086";
        String token = "uKpwc864TxyzE38IkIzYUZFU4ELjdHkZFO83tnBFMdMXx6eqjAUFOlbRbWzYRorLK9i9w6zqpQojmi2hutv6Zw==";
        String org = "nvt";
        return InfluxDBClientFactory.create(url, token.toCharArray(), org);
    }
}
