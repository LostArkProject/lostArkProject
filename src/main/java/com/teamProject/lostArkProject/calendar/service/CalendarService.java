package com.teamProject.lostArkProject.calendar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.calendar.domain.Calendar;
import com.teamProject.lostArkProject.calendar.domain.Item;
import com.teamProject.lostArkProject.calendar.domain.RewardItem;
import com.teamProject.lostArkProject.calendar.domain.StartTime;
import com.teamProject.lostArkProject.calendar.dto.CalendarAPIDTO;
import com.teamProject.lostArkProject.calendar.dto.ItemDTO;
import com.teamProject.lostArkProject.calendar.dto.CalendarWithServerTimeDTO;
import com.teamProject.lostArkProject.calendar.mapper.CalendarMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
    private final CalendarMapper calendarMapper;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // api에서 주간일정 데이터를 받아와서 db에 저장
    public Mono<Void> getAndSaveCalendar() {
        return webClient.get()
                .uri("/gamecontents/calendar")
                .retrieve()
                .bodyToMono(String.class)
                .map(apiResponse -> {
                    try {
                        return objectMapper.readValue(apiResponse, new TypeReference<List<CalendarAPIDTO>>() {});
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .flatMapMany(Flux::fromIterable)  // List를 Flux로 변환
                .map(this::toEntity)  // CalendarAPIDTO를 Calendar 엔티티로 변환
                .flatMap(calendar -> Mono.fromCallable(() -> {
                    // Calendar 저장
                    int calendarResult = calendarMapper.insertCalendar(calendar);
                    logger.info("(CalendarService) Insert Calendar Data Result: {}", calendarResult);
                    // Calendar의 calendar_id를 StartTime, RewardItem에 할당
                    calendar.getStartTimes().forEach(startTime -> startTime.setCalendarId(calendar.getCalendarId()));
                    calendar.getRewardItems().forEach(rewardItem -> rewardItem.setCalendarId(calendar.getCalendarId()));

                    // Calendar의 StartTime 저장
                    int startTimeResult = calendarMapper.insertStartTime(calendar.getStartTimes());
                    logger.info("(CalendarService) Insert StartTime Data Result: {}", startTimeResult);

                    // Calendar의 RewardItem 저장
                    int RewardItemResult = calendarMapper.insertRewardItem(calendar.getRewardItems());
                    logger.info("(CalendarService) Insert RewardItem Data Result: {}", RewardItemResult);

                    // Calendar.RewardItem의 Item 저장
                    calendar.getRewardItems()
                            .forEach(rewardItem -> calendarMapper.insertItem(rewardItem.getItems()));
                    logger.info("(CalendarService) Insert Item Data Result: {}", calendar.getRewardItems().size());

                    return calendarResult;
                }))
                .then()  // Mono로 변환 (doOnNext() 메서드로 result를 버리는 게 아니라 사용하도록 만들 수 있음)
                .onErrorResume(e -> {
                    logger.error("(CalendarService) Error occured while saving calendar data", e);
                    return Mono.empty();
                });
    }

    // 주간일정 데이터를 db에서 가져와서 반환
    public Mono<List<Calendar>> getCalendars() {
        return Mono.fromCallable(() -> calendarMapper.selectCalendar());
    }

    // 주간일정 데이터에 남은시간 데이터를 추가해서 반환
    public Mono<List<CalendarWithServerTimeDTO>> getCalendarWithRemainTime() {
        return Mono.fromCallable(() -> {
            List<Calendar> calendars = calendarMapper.selectCalendar();
            return calendars.stream()
                    .map(this::convertToDTO)
                    .toList();
        });
    }

    // api에서 받아온 데이터를 entity로 변환
    private Calendar toEntity(CalendarAPIDTO apiDTO) {
        Calendar calendar = new Calendar();
        calendar.setCategoryName(apiDTO.getCategoryName());
        calendar.setContentsName(apiDTO.getContentsName());
        calendar.setContentsIcon(apiDTO.getContentsIcon());
        calendar.setMinItemLevel(apiDTO.getMinItemLevel());
        calendar.setLocation(apiDTO.getLocation());

        // 외부 api에서 받아온 데이터인 rewardItemAPIDTO를 Calendar의 RewardItem으로 변환
        List<RewardItem> rewardItems = apiDTO.getRewardItems().stream().map(rewardItemAPIDTO -> {
            RewardItem rewardItem = new RewardItem();
            rewardItem.setItemLevel(rewardItemAPIDTO.getItemLevel());

            // 외부 api에서 받아온 데이터인 ItemAPIDTO를 Calendar.RewardItem의 Item으로 변환
            List<Item> items = rewardItemAPIDTO.getItems().stream().map(itemAPIDTO -> {
                Item item = new Item();
                item.setName(itemAPIDTO.getName());
                item.setIcon(itemAPIDTO.getIcon());
                item.setGrade(itemAPIDTO.getGrade());
                return item;
            }).toList();

            rewardItem.setItems(items);
            return rewardItem;
        }).toList();

        calendar.setRewardItems(rewardItems);

        // <LocalDate> 배열의 StartTimeAPIDTO의 startTimes를 <StartTime> 객체 배열의 startTimes로 변환
        List<StartTime> startTimes = apiDTO.getStartTimes().stream().map(startTime -> {
            StartTime start = new StartTime();
            start.setStartTime(startTime);

            return start;
        }).toList();

        calendar.setStartTimes(startTimes);

        return calendar;
    }

    // db의 entity를 CalendarWithServerTimeDTO 객체로 변환
    private CalendarWithServerTimeDTO convertToDTO(Calendar calendar) {
        CalendarWithServerTimeDTO dto = new CalendarWithServerTimeDTO();
        dto.setCategoryName(calendar.getCategoryName());
        dto.setContentsName(calendar.getContentsName());
        dto.setSanitizedContentsName(sanitizeContentsName(calendar.getContentsName()));
        dto.setContentsIcon(calendar.getContentsIcon());
        dto.setMinItemLevel(calendar.getMinItemLevel());

        List<LocalDateTime> startTimes = calendar.getStartTimes()
                .stream()
                .map(StartTime::getStartTime)
                .toList();
        dto.setStartTimes(startTimes);

        dto.setServerTime(LocalDateTime.now());
        dto.setLocation(calendar.getLocation());

        List<ItemDTO> items = calendar.getRewardItems().stream()  // Calendar의 RewardItems를 stream 변환
                .flatMap(rewardItem -> rewardItem.getItems().stream())  // RewardItem의 Items를 stream 변환
                .map(this::convertToItemDTO)  // Item 객체를 형변환하는 메서드
                .toList();
        dto.setItems(items);

        return dto;
    }

    // Calendar의 Item 객체를 CalendarWithServerTimeDTO의 ItemDTO 객체로 변환
    private ItemDTO convertToItemDTO(Item item) {
        ItemDTO itemDTO = new ItemDTO();
        itemDTO.setName(item.getName());
        itemDTO.setIcon(item.getIcon());
        itemDTO.setGrade(item.getGrade());

        return itemDTO;
    }

    // contentsName의 모든 특수문자를 _로 변환
    private String sanitizeContentsName(String contentsName) {
        return contentsName.replaceAll("[^a-zA-Z0-9가-힣]", "_");
    }


/*    // 남은 시간 계산
    private Duration remainTime(List<LocalDateTime> startTimes) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime remainTime = startTimes.stream()
                .filter(startTime -> startTime.isAfter(now))
                .min(LocalDateTime::compareTo)
                .orElse(null);

        if (remainTime != null) {
            return Duration.between(now, remainTime);
        } else {
            return null;
        }
    }

    private Map<String, Calendar> remainTimes = new HashMap<>();

    public Mono<Map<String, Calendar>> getRemainTimes() {
        return Mono.just(remainTimes);
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
    }*/
}