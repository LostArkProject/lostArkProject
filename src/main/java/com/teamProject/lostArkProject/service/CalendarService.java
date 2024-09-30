package com.teamProject.lostArkProject.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.domain.Calendar;
import com.teamProject.lostArkProject.domain.Item;
import com.teamProject.lostArkProject.dto.ItemDTO;
import com.teamProject.lostArkProject.dto.CalendarDTO;
import com.teamProject.lostArkProject.repository.CalendarMemoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public Mono<List<CalendarDTO>> getCalendarWithRemainTime() {
        return calendarRepository.getCalendar() // Mono<List<Calendar>> 반환
                .flatMap(calendars -> {
                    List<CalendarDTO> calendarDTOs = calendars.stream()
                            .map(this::convertToDTO) // Calendar -> CalendarDTO 변환
                            .collect(Collectors.toList());
                    return Mono.just(calendarDTOs); // DTO 리스트를 Mono로 반환
                });
    }

    // CalendarDTO 객체로 변환
    private CalendarDTO convertToDTO(Calendar calendar) {
        CalendarDTO dto = new CalendarDTO();
        dto.setCategoryName(calendar.getCategoryName());
        dto.setContentsName(calendar.getContentsName());
        dto.setContentsIcon(calendar.getContentsIcon());
        dto.setMinItemLevel(calendar.getMinItemLevel());
        dto.setStartTimes(calendar.getStartTimes());
        dto.setRemainTime(remainTime(calendar.getStartTimes()));
        dto.setLocation(calendar.getLocation());

        List<ItemDTO> items = calendar.getRewardItems().stream()  // Calendar의 RewardItems를 stream 변환
                .flatMap(rewardItem -> rewardItem.getItems().stream())  // RewardItem의 Items를 stream 변환
                .map(this::convertToItemDTO)  // Item 객체를 형변환하는 메서드
                .toList();
        dto.setItems(items);

        return dto;
    }

    // 남은 시간 계산
    private LocalDateTime remainTime(List<LocalDateTime> startTimes) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime remainTime = startTimes.stream()
                .filter(startTime -> startTime.isAfter(now))
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (remainTime != null) {
            Duration duration = Duration.between(now, remainTime);
            return remainTime.plus(duration);
        } else {
            return null;
        }
    }

    // Calendar의 Item 객체를 CalendarDTO의 ItemDTO 객체로 변환
    private ItemDTO convertToItemDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setName(item.getName());
        itemDTO.setIcon(item.getIcon());
        itemDTO.setGrade(item.getGrade());

        return itemDTO;
    }
}