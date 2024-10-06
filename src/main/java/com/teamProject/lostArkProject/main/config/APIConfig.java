package com.teamProject.lostArkProject.main.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
@PropertySource("classpath:config.properties")
public class APIConfig {
    @Value("${lostArkAPIKey}")
    private String lostArkAPIKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB로 설정
                .baseUrl("https://developer-lostark.game.onstove.com")
                .defaultHeader("Authorization", "Bearer " + lostArkAPIKey)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // 클라이언트에서 받는 날짜 데이터 형식 수정
        return objectMapper;
    }
}