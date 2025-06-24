package com.server.ShareDoo.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5)) // Timeout kết nối: 5 giây
                .setReadTimeout(Duration.ofSeconds(15))    // Timeout đọc dữ liệu: 15 giây
                .build();
    }
}