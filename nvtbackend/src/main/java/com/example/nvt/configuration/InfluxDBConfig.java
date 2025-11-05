package com.example.nvt.configuration;


import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@RequiredArgsConstructor
public class InfluxDBConfig {

    private final InfluxProperties influxProperties;

    @Bean
    public InfluxDBClient customInfluxDBClient() {
        return InfluxDBClientFactory.create(
                influxProperties.getUrl(),
                influxProperties.getToken().toCharArray(),
                influxProperties.getOrg()
        );
    }
}
