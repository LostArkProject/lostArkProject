package com.teamProject.lostArkProject.collectible.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.collectible.dao.CollectibleDAO;
import com.teamProject.lostArkProject.collectible.domain.CharacterInfo;
import com.teamProject.lostArkProject.collectible.domain.CollectiblePoint;
import com.teamProject.lostArkProject.collectible.dto.CollectiblePointDTO;
import com.teamProject.lostArkProject.collectible.dto.CollectiblePointSummaryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@Slf4j
public class CollectibleService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final CollectibleDAO collectibleDAO;

    public CollectibleService(WebClient webClient, ObjectMapper objectMapper, CollectibleDAO collectibleDAO) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.collectibleDAO = collectibleDAO;
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
    public void saveCollectiblePoint(String characterName) {
        webClient.get()
                .uri("/armories/characters/" + characterName + "/collectibles") // 실제 API의 경로로 변경
                .retrieve()
                .bodyToMono(String.class) // CollectibleItem으로 매핑
                .flatMap(response -> {
                    ObjectMapper objectMapper = new ObjectMapper();
                    try {
                        // API 응답을 DTO 리스트로 변환
                        List<CollectiblePointDTO> allItems = objectMapper.readValue(response, new TypeReference<List<CollectiblePointDTO>>() {
                        });

                        // DTO 리스트를 도메인 리스트로 변환
                        List<CollectiblePoint> filterItem = allItems.stream()
                                .flatMap(item -> item.getCollectiblePoints().stream()
                                        .map(detail -> {
                                            CollectiblePoint collectible = new CollectiblePoint();
                                            collectible.setMemberId(characterName);
                                            collectible.setCollectibleTypeName(item.getCollectibleTypeName());
                                            collectible.setCollectibleIconLink(item.getCollectibleIconLink());
                                            collectible.setCollectiblePointName(detail.getCollectiblePointName());
                                            collectible.setCollectedPoint(detail.getCollectedPoint());
                                            collectible.setCollectibleMaxPoint(detail.getCollectibleMaxPoint());
                                            return collectible;
                                        })
                                )
                                .toList();

                        // 데이터베이스에 저장
                        filterItem.forEach(collectibleDAO::insertCollectiblePoint);

                        // 저장 완료 후 빈 Mono 반환
                        return Mono.empty();
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                }).subscribe();
    }

    public List<CollectiblePointSummaryDTO> getCollectiblePointSummary(String memberId) {
        return collectibleDAO.getCollectiblePointSummary(memberId);
    }
}