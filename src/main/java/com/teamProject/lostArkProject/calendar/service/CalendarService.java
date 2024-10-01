package com.teamProject.lostArkProject.calendar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.calendar.domain.Calendar;
import com.teamProject.lostArkProject.calendar.domain.Item;
import com.teamProject.lostArkProject.calendar.dto.ItemDTO;
import com.teamProject.lostArkProject.calendar.dto.CalendarDTO;
import com.teamProject.lostArkProject.calendar.mapper.CalendarMemoryRepository;
import com.teamProject.lostArkProject.collectible.domain.CharacterInfo;
import com.teamProject.lostArkProject.collectible.domain.CollectibleItem;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
    private final CalendarMemoryRepository calendarRepository;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // api에서 주간일정 데이터를 받아와서 db에 저장
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

    // 주간일정 데이터를 db에서 가져와서 반환
    public Mono<List<Calendar>> getCalendars() {
        return calendarRepository.getCalendar();
    }

    // 주간일정 데이터에 남은시간 데이터를 추가해서 반환
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


    private Map<String, Calendar> remainTimes = new HashMap<>();

    public Mono<Map<String, Calendar>> getRemainTimes() {
        return Mono.just(remainTimes);
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
                .flatMap(apiResponse -> Mono.fromCallable(() -> {  // mapping 방식을 비동기 처리로 변경
                    try {
                        List<Calendar> calendars = objectMapper.readValue(apiResponse, new TypeReference<List<Calendar>>() {
                        });

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
                        return calendars;
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse calendar", e);
                    }
                }));
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