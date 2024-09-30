package com.teamProject.lostArkProject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.domain.Calendar;
import com.teamProject.lostArkProject.dto.CalendarDTO;
import com.teamProject.lostArkProject.repository.CalendarMemoryRepository;
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
    private final CalendarMemoryRepository calendarRepository;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public Mono<Void> getAndSaveCalendar() {
        return webClient.get()
                .uri("/gamecontents/calendar")
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(apiResponse -> Mono.fromCallable(() -> {  // mapping 방식을 비동기 처리로 변경
                    try {
                        List<Calendar> calendars = objectMapper.readValue(apiResponse, new TypeReference<>() {});
                        return calendars;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse calendar", e);
                    }
                }))
                .flatMap(calendars -> {
                    calendarRepository.saveCalendar(calendars);
                    return Mono.empty();
                });
    }

    public Mono<List<Calendar>> getCalendars() {
        return calendarRepository.getCalendar();
    }

    public CalendarDTO convertToDTO(Calendar calendar) {
        CalendarDTO dto = new CalendarDTO();
        dto.setCategoryName();
        dto.setContentsName();
        dto.setContentsIcon();
        dto.setMinItemLevel();
        dto.setStartTimes();
        dto.setRemainTime();
        dto.setLocation();
        dto.setItems();

        return dto;
    }
}
