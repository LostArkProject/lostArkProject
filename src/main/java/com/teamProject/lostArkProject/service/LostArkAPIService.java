package com.teamProject.lostArkProject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.teamProject.lostArkProject.domain.Calendar;
import com.teamProject.lostArkProject.domain.CharacterInfo;
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
        objectMapper.registerModule(new JavaTimeModule());
    }

    public Mono<List<CharacterInfo>> getCharacterInfo(String characterName) {
        return webClient.get()
                .uri("/characters/" + characterName + "/siblings")
                .retrieve()
                .bodyToMono(String.class)
                /* flatMap(): json 방식의 데이터를 CharacterInfo 타입의 List로 변환하는 역할을 하는 메소드
                 *
                 * 1. API 요청 결과로 얻은 json 문자열의 키를 ObjectMapper를 사용하여 CharacterInfo 클래스의 필드와 매핑
                 * 2. json의 각 요소가 CharacterInfo 클래스의 인스턴스로 변환 (이 인스턴스의 필드에는 json에서 추출한 값들이 할당)
                 * 3. CharacterInfo 객체들이 모여 하나의 List<CharacterInfo>를 생성하고 characterInfos 변수에 저장
                 * 4. getter 메소드를 이용해 각 필드의 값을 추출할 수 있음
                 */
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

                        // 값 확인용 로그
                        logger.info(now.format(DateTimeFormatter.ISO_DATE_TIME));
                        logger.info("First ContentsName: {}", calendars.get(1).getContentsName());
                        logger.info("First StartTimes: {}", calendars.get(1).getStartTimes());
                        logger.info("Min remainTime: {}", calendars.get(1).getRemainTime());
                        logger.info("Map Check: {}", remainTimes.get("모험 섬").getRemainTime());

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
}