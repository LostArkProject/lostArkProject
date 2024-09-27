package com.teamProject.lostArkProject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.domain.Calendar;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private static final Logger logger = LoggerFactory.getLogger(LostArkAPIService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public Mono<List<Calendar>> getCalendar() {

        return webClient.get()
                .uri("/gamecontents/calendar")
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(apiResponse -> Mono.fromCallable(() -> {  // mapping 방식을 비동기 처리로 변경
                    try {
                        return objectMapper.readValue(apiResponse, new TypeReference<>() {});
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse calendar", e);
                    }
                }));
    }
}
