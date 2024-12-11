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
        String token = "4bkMeBSwq720tcQDjYxKFmS7H-fsRH7Bzmx8aSkgiryMRKFxhP3ij0EnY6BGsnelO71Paj7-5_Pujkw24H3u_A==";
        String org = "nvt";
        return InfluxDBClientFactory.create(url, token.toCharArray(), org);
    }
}
