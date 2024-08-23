package com.teamProject.lostArkProject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
@PropertySource("classpath:config.properties")
public class LostArkAPIConfig {
    @Value("${lostArkAPIKey}")
    private String lostArkAPIKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://developer-lostark.game.onstove.com")
                .defaultHeader("Authorization", "Bearer " + lostArkAPIKey) // 여기에 자신의 API 키를 넣어야 합니다.
                .build();
    }
}