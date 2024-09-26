package com.teamProject.lostArkProject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.teamProject.lostArkProject.domain.Calendar;
import com.teamProject.lostArkProject.domain.CharacterInfo;
import com.teamProject.lostArkProject.domain.CollectibleItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LostArkAPIService {
    // 콘솔에 로그 찍어주는 객체
    private static final Logger logger = LoggerFactory.getLogger(LostArkAPIService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    private Map<String, Calendar> remainTimes = new HashMap<>();

    public Mono<Map<String, Calendar>> getRemainTimes() {
        return Mono.just(remainTimes);
    }

    public LostArkAPIService(WebClient webClient, ObjectMapper objectMapper) {
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

    // 주간일정 가져오는 메소드
    public Mono<List<Calendar>> getCalendar() {
        // remainTimes 초기화 (남은 시간을 갱신하기 위함)
        remainTimes.clear();

        return webClient.get()
                .uri("/gamecontents/calendar")
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(apiResponse -> {
                    try {
                        List<Calendar> calendars = objectMapper.readValue(apiResponse, new TypeReference<List<Calendar>>() {});

                        LocalDateTime now = LocalDateTime.now();

                        for (Calendar calendar : calendars) {
                            List<LocalDateTime> startTimes = calendar.getStartTimes();
                            if (startTimes == null) {
                                calendar.setRemainTime("출현 대기중");
                                continue;
                            }

                            // StartTimes가 이미 지났다면 필터링
                            Optional<LocalDateTime> nextStartTimeOpt = startTimes.stream()
                                    .filter(startTime -> startTime.isAfter(now))
                                    .findFirst();

                            if (nextStartTimeOpt.isPresent()) {
                                LocalDateTime nextStartTime = nextStartTimeOpt.get();
                                Duration duration = Duration.between(now, nextStartTime);
                                long hours = duration.toHours();
                                long minutes = duration.toMinutes() % 60;
                                long seconds = duration.getSeconds() % 60;
                                calendar.setRemainTime(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                            } else {
                                calendar.setRemainTime("출현 대기중");
                            }

                            // 카테고리별로 가장 적은 remainTime 찾기
                            String categoryName = calendar.getCategoryName();
                            if (!remainTimes.containsKey(categoryName) ||
                                    isLessRemainTime(calendar, remainTimes.get(categoryName), now)) {
                                remainTimes.put(categoryName, calendar);
                            }
                        }
                        return Mono.just(calendars);
                    } catch (Exception e) {
                        return Mono.error(new RuntimeException("Failed to parse calendar", e));
                    }
                });
    }

    // remainTime 비교를 위한 메소드
    private boolean isLessRemainTime(Calendar calendar, Calendar existingCalendar, LocalDateTime now) {
        LocalDateTime newRemainTime = getNextStartTime(calendar, now);
        LocalDateTime existingRemainTime = getNextStartTime(existingCalendar, now);
        return newRemainTime.isBefore(existingRemainTime);
    }

    // 다음 시작 시간을 가져오는 메소드
    private LocalDateTime getNextStartTime(Calendar calendar, LocalDateTime now) {
        List<LocalDateTime> startTimes = calendar.getStartTimes();
        return startTimes.stream()
                .filter(startTime -> startTime.isAfter(now))
                .findFirst()
                .orElse(LocalDateTime.MAX);
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