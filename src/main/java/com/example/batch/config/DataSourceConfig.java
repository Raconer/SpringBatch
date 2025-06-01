package com.example.batch.config;

import com.p6spy.engine.spy.P6DataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Bean
    @Primary
    public DataSource p6spyDataSource(DataSourceProperties properties) {
        DataSource actualDataSource = properties.initializeDataSourceBuilder().build();
        return new P6DataSource(actualDataSource); // 감싸기
    }
}
