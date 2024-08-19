package com.teamProject.lostArkProject.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class LostArkAPIConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl("https://developer-lostark.game.onstove.com")
                .defaultHeader("Authorization", "Bearer 여기") // 여기에 자신의 API 키를 넣어야 합니다.
                .build();
    }
}