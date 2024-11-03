package com.teamProject.lostArkProject.calendar.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.calendar.dao.CalendarDAO;
import com.teamProject.lostArkProject.calendar.domain.Calendar;
import com.teamProject.lostArkProject.calendar.domain.Item;
import com.teamProject.lostArkProject.calendar.domain.RewardItem;
import com.teamProject.lostArkProject.calendar.domain.StartTime;
import com.teamProject.lostArkProject.calendar.dto.CalendarAPIDTO;
import com.teamProject.lostArkProject.calendar.dto.CalendarWithServerTimeDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private static final Logger logger = LoggerFactory.getLogger(CalendarService.class);
    private final CalendarDAO calendarDAO;

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
                    int calendarResult = calendarDAO.insertCalendar(calendar);
                    logger.info("(CalendarService) Insert Calendar Data Result: {}", calendarResult);

                    // Calendar의 calendar_id를 StartTime, RewardItem에 할당
                    calendar.getStartTimes().forEach(startTime ->
                            startTime.setCalendarId(calendar.getCalendarId())
                    );
                    calendar.getRewardItems().forEach(rewardItem ->
                            rewardItem.setCalendarId(calendar.getCalendarId())
                    );

                    // Calendar의 StartTime 저장
                    int startTimeResult = calendarDAO.insertStartTime(calendar.getStartTimes());
                    logger.info("(CalendarService) Insert StartTime Data Result: {}", startTimeResult);

                    // Calendar의 RewardItem 저장
                    int RewardItemResult = calendarDAO.insertRewardItem(calendar.getRewardItems());
                    logger.info("(CalendarService) Insert RewardItem Data Result: {}", RewardItemResult);

                    // RewardItem의 reward_item_id를 Item에 할당
                    calendar.getRewardItems().forEach(rewardItem ->
                            rewardItem.getItems().forEach(item ->
                                    item.setRewardItemId(rewardItem.getRewardItemId())
                            )
                    );

                    // Calendar.RewardItem의 Item 저장
                    calendar.getRewardItems()
                            .forEach(rewardItem -> calendarDAO.insertItem(rewardItem.getItems()));
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
        return Mono.fromCallable(() -> calendarDAO.selectAllCalendarTable());
    }

    // Calendar에 서버시간을 추가해서 반환
    public Mono<List<CalendarWithServerTimeDTO>> getCalendarWithServerTime() {
        return Mono.fromCallable(() -> calendarDAO.selectCalendarWithStartTime()
                    .stream()
                    .map(this::convertDTOWithLongTypeTime)
                    .toList()
        );
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

    // 서버 시간, 이벤트 시작 시간을 long 타입 변환
    private CalendarWithServerTimeDTO convertDTOWithLongTypeTime(Calendar calendar) {
        CalendarWithServerTimeDTO calendarDTO = new CalendarWithServerTimeDTO();
        LocalDateTime now = LocalDateTime.now();  // 현재 서버 시간을 저장

        calendarDTO.setCategoryName(calendar.getCategoryName());
        calendarDTO.setContentsName(calendar.getContentsName());
        calendarDTO.setSanitizedContentsName(sanitizeContentsName(calendar.getContentsName()));
        calendarDTO.setContentsIcon(calendar.getContentsIcon());
        calendarDTO.setMinItemLevel(calendar.getMinItemLevel());

        List<Long> startTime = calendar.getStartTimes()  // Calendar 객체의 startTimes(List<StartTime>) 필드를 가져옴
                .stream()  // List를 stream으로 변환
                .map(StartTime::getStartTime)  // StartTime 객체의 startTime(LocalDateTime) 필드를 가져옴
                .map(localDateTime -> localDateTime.toInstant(ZoneOffset.ofHours(9)).toEpochMilli())  // LocalDateTime 타입을 밀리초(long)로 변환
                .toList();  // stream을 List로 변환
        calendarDTO.setStartTimes(startTime);

        calendarDTO.setServerTime(now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli());  // 서버 시간을 밀리초(long)로 변환

        return calendarDTO;
    }

    // contentsName의 모든 특수문자를 _로 변환
    private String sanitizeContentsName(String contentsName) {
        return contentsName.replaceAll("[^a-zA-Z0-9가-힣]", "_");
    }
}