package com.teamProject.lostArkProject.collectible.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.collectible.domain.CharacterInfo;
import com.teamProject.lostArkProject.collectible.domain.CollectibleItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class CollectibleService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public CollectibleService(WebClient webClient, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
    }

    public Mono<List<CharacterInfo>> getCharacterInfo(String characterName) {
        return webClient.get()
                .uri("/characters/" + characterName + "/siblings")
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(apiResponse -> {
                    try {
                        List<CharacterInfo> characterInfos = objectMapper.readValue(apiResponse, new TypeReference<List<CharacterInfo>>() {});
                        return Mono.just(characterInfos);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }

    // 내실 내용 가져오는 메소드
    public Mono<List<CollectibleItem>> getCharacterCollectible(String characterName) {
        return webClient.get()
                .uri("/armories/characters/" + characterName + "/collectibles") // 실제 API의 경로로 변경
                .retrieve()
                .bodyToMono(String.class) // CollectibleItem으로 매핑
                .flatMap(response -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        List<CollectibleItem> collectibleItem = objectMapper.readValue(response, new TypeReference<List<CollectibleItem>>() {});
                        return Mono.just(collectibleItem);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                });
    }
}