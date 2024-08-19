package com.teamProject.lostArkProject.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class LostArkAPIService {

    private final WebClient webClient;

    public LostArkAPIService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> getCharacterInfo() {
        return webClient.get()
                .uri("/characters/ACocg/siblings") // 캐릭터 이름을 ACocg로 고정
                .retrieve()
                .bodyToMono(String.class);
    }
}
