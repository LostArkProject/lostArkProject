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

    /*
     * 주간 일정표를 가져올 시 메모리 buffer의 크기를 초과하는 데이터가 응답되는 문제 발생
     * 따라서 WebClient의 메모리 최대 크기를 늘리는 코드를 추가
     */
    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB로 설정
                .baseUrl("https://developer-lostark.game.onstove.com")
                .defaultHeader("Authorization", "Bearer " + lostArkAPIKey)
                .build();
    }
}