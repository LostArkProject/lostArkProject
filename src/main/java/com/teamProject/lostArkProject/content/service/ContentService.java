package com.teamProject.lostArkProject.content.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.content.dao.ContentDAO;
import com.teamProject.lostArkProject.content.domain.Content;
import com.teamProject.lostArkProject.content.domain.Reward;
import com.teamProject.lostArkProject.content.domain.StartTime;
import com.teamProject.lostArkProject.content.dto.ContentDTO;
import com.teamProject.lostArkProject.content.dto.api.CalendarApiDTO;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContentService {
    private static final Logger logger = LoggerFactory.getLogger(ContentService.class);
    private final ContentDAO contentDAO;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    // 외부 api에서 Calendar 데이터를 받아서 CalendarApiDTO 객체에 매핑하는 메서드
    public Mono<List<CalendarApiDTO>> fetchCalendarsFromApi() {
        logger.info("fetchCalendarsFromApi 메서드 호출");

        return webClient.get()
                .uri("/gamecontents/calendar")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<CalendarApiDTO>>() {});
        /**
         *  bodyToMono() 혹은 bodyToFlux()는 외부 api의 응답 데이터를 처리하는 방식에서 차이가 있습니다.
         *
         *  1. bodyToMono()
         *      - bodyToMono()는 응답 데이터를 한 번에 가져옵니다.
         *      - 매개변수로는 가져오는 데이터의 데이터 타입(String.class 혹은 ParameterizedTypeReperence)을 입력합니다.
         *      - bodyToMono(String.class):
         *          응답 데이터를 String (JSON 텍스트) 타입으로 반환받습니다.
         *          이 방식은 텍스트이기 때문에 로그 출력 등의 작업을 할 때는 간단하다는 장점이 있습니다.
         *          하지만 데이터를 실제로 활용하기 위해선 ObjectMapper 객체로 매핑하는 과정이 추가로 필요합니다.
         *
         *      - bodyToMono(new ParameterizedTypeReference<List<Obj>>() {}:
         *          응답 데이터를 List (Java 객체)로 반환받습니다.
         *          응답 데이터의 스펙이 [ ... ] 등의 배열로 시작하는 경우에 적합합니다.
         *          데이터를 객체에 매핑하기 때문에, 이 데이터는 바로 사용할 수 있습니다.
         *          객체에 매핑할 때는 각 필드에 @JsonProperty() 애노테이션을 사용합니다. 애노테이션에는 외부 API의 필드명을 입력합니다.
         *
         *  2. bodyToFlux()
         *      - bodyToFlux()는 응답 데이터를 스트리밍 형식으로 처리합니다.
         *      - 즉 모든 데이터를 한 번에 받아오지 않고 순차적으로 처리합니다.
         *      - bodyToFlux()는 비동기 방식으로 동작하며, 리액티브 프로그래밍의 "구독(subscribe)" 모델을 통해 데이터를 처리합니다.
         *
         *      - 응답받는 데이터가 매우 많은 경우에 적절하지만, 이 데이터에 접근하는 방식이 복잡합니다.
         *      - 따라서 응답받는 데이터의 양이 적다면 비추천합니다.
         */
    }

    // 받아온 Calendar 데이터를 프로젝트의 도메인 형식으로 변환 후 db에 저장
    @Transactional
    public Mono<String> saveContent() {
        return Mono.fromRunnable(() -> {
            contentDAO.deleteStartTime();
            contentDAO.deleteReward();
            contentDAO.deleteContent();
            logger.info("저장되어 있는 모든 Content 데이터 삭제");
        })
        .then(fetchCalendarsFromApi()) // Mono<List<...>>
        .flatMapMany(Flux::fromIterable) // Mono<List<...>>를 Flux<...>로 변환
        .map(this::toDomain) // api 객체를 도메인 객체로 변환하는 메서드 호출
        .flatMap(this::saveToDatabase) // 변환된 데이터를 db에 저장하는 메서드 호출
        .then(Mono.just("Content 데이터가 성공적으로 저장되었습니다."))
        .onErrorResume(e -> {
            logger.error("Content 데이터 저장 중 에러가 발생했습니다. \n{}", e.getMessage());
            return Mono.error(e);
        });
    }

    // content 테이블의 모든 데이터 조회
    public List<ContentDTO> getContentsAll() {
        logger.info("getContentsAll() 호출");
        return contentDAO.getContentsAll();
    }

    // db에 저장하는 메서드
    @Transactional
    protected Mono<Void> saveToDatabase(Content content) {
        logger.info("saveToDatabase 메서드 호출");
        
        return Mono.fromRunnable(() -> {
            contentDAO.saveContent(content);
            contentDAO.saveStartTime(content.getStartTimes());
            contentDAO.saveReward(content.getRewards());
        });
    }

    // api 데이터를 도메인 객체 형식으로 변환하는 메서드
    private Content toDomain(CalendarApiDTO calendarApiDTO) {
        logger.info("toDomain 호출");
        Content content = new Content();

        content.setContentName(calendarApiDTO.getContentsName());
        content.setContentIconLink(calendarApiDTO.getContentsIcon());
        content.setMinItemLevel(calendarApiDTO.getMinItemLevel());
        content.setContentLocation(calendarApiDTO.getLocation());
        content.setContentCategory(calendarApiDTO.getCategoryName());

        // api에서 받아온 StartTimes 데이터를 db의 start_times 테이블에 맞게 가공
        List<StartTime> startTimes = calendarApiDTO.getStartTimes().stream()
                .map(startTime -> {
                    StartTime st = new StartTime();
                    st.setContentName(content.getContentName());
                    st.setContentStartTime(LocalDateTime.parse(startTime));

                    return st;
            }).toList();
        content.setStartTimes(startTimes);


        // api에서 받아온 RewardItems 데이터를 db의 reward 테이블에 맞게 가공
        List<Reward> rewards = calendarApiDTO.getRewardItems().stream()
                .flatMap(rewardItem -> rewardItem.getItems().stream()
                        // 아이템 이름이 실링이거나 전투 각인서이고, 컨텐츠 시작시간이 null이라면 매핑 제외
                        .filter(item -> !((item.getName().equals("실링") ||
                                           item.getName().equals("전투 각인서")) || 
                                           item.getName().equals("해적 주화") && item.getStartTimes() == null))
                        .map(item -> {
                            Reward reward = new Reward();

                            reward.setContentName(content.getContentName());
                            reward.setRewardItemName(item.getName());
                            reward.setRewardItemLevel(rewardItem.getItemLevel());
                            reward.setRewardItemIconLink(item.getIcon());
                            reward.setRewardItemGrade(item.getGrade());

                            return reward;
                        })
                ).toList();
        content.setRewards(rewards);

        return content;
    }

    //// 주간일정 데이터를 db에서 가져와서 반환
    //public Mono<List<Calendar>> getCalendars() {
    //    return Mono.fromCallable(() -> calendarDAO.selectAllCalendarTable());
    //}
    //
    //// Calendar에 서버시간을 추가해서 반환
    //public Mono<List<CalendarWithServerTimeDTO>> getCalendarWithServerTime() {
    //    return Mono.fromCallable(() -> calendarDAO.selectCalendarWithStartTime()
    //                .stream()
    //                .map(this::convertDTOWithLongTypeTime)
    //                .toList()
    //    );
    //}
    //
    //// api에서 받아온 데이터를 entity로 변환
    //private Calendar toEntity(CalendarAPIDTO apiDTO) {
    //    Calendar calendar = new Calendar();
    //    calendar.setCategoryName(apiDTO.getCategoryName());
    //    calendar.setContentsName(apiDTO.getContentsName());
    //    calendar.setContentsIcon(apiDTO.getContentsIcon());
    //    calendar.setMinItemLevel(apiDTO.getMinItemLevel());
    //    calendar.setLocation(apiDTO.getLocation());
    //
    //    // 외부 api에서 받아온 데이터인 rewardItemAPIDTO를 Calendar의 RewardItem으로 변환
    //    List<RewardItem> rewardItems = apiDTO.getRewardItems().stream().map(rewardItemAPIDTO -> {
    //        RewardItem rewardItem = new RewardItem();
    //        rewardItem.setItemLevel(rewardItemAPIDTO.getItemLevel());
    //
    //        // 외부 api에서 받아온 데이터인 ItemAPIDTO를 Calendar.RewardItem의 Item으로 변환
    //        List<Item> items = rewardItemAPIDTO.getItems().stream().map(itemAPIDTO -> {
    //            Item item = new Item();
    //            item.setName(itemAPIDTO.getName());
    //            item.setIcon(itemAPIDTO.getIcon());
    //            item.setGrade(itemAPIDTO.getGrade());
    //            return item;
    //        }).toList();
    //
    //        rewardItem.setItems(items);
    //        return rewardItem;
    //    }).toList();
    //
    //    calendar.setRewardItems(rewardItems);
    //
    //    // <LocalDate> 배열의 StartTimeAPIDTO의 startTimes를 <StartTime> 객체 배열의 startTimes로 변환
    //    List<StartTime> startTimes = apiDTO.getStartTimes().stream().map(startTime -> {
    //        StartTime start = new StartTime();
    //        start.setStartTime(startTime);
    //
    //        return start;
    //    }).toList();
    //
    //    calendar.setStartTimes(startTimes);
    //
    //    return calendar;
    //}

    //// 서버 시간, 이벤트 시작 시간을 long 타입 변환
    //private CalendarWithServerTimeDTO convertDTOWithLongTypeTime(Calendar calendar) {
    //    CalendarWithServerTimeDTO calendarDTO = new CalendarWithServerTimeDTO();
    //    LocalDateTime now = LocalDateTime.now();  // 현재 서버 시간을 저장
    //
    //    calendarDTO.setCategoryName(calendar.getCategoryName());
    //    calendarDTO.setContentsName(calendar.getContentsName());
    //    calendarDTO.setSanitizedContentsName(sanitizeContentsName(calendar.getContentsName()));
    //    calendarDTO.setContentsIcon(calendar.getContentsIcon());
    //    calendarDTO.setMinItemLevel(calendar.getMinItemLevel());
    //
    //    List<Long> startTime = calendar.getStartTimes()  // Calendar 객체의 startTimes(List<StartTime>) 필드를 가져옴
    //            .stream()  // List를 stream으로 변환
    //            .map(StartTime::getStartTime)  // StartTime 객체의 startTime(LocalDateTime) 필드를 가져옴
    //            .map(localDateTime -> localDateTime.toInstant(ZoneOffset.ofHours(9)).toEpochMilli())  // LocalDateTime 타입을 밀리초(long)로 변환
    //            .toList();  // stream을 List로 변환
    //    calendarDTO.setStartTimes(startTime);
    //
    //    calendarDTO.setServerTime(now.toInstant(ZoneOffset.ofHours(9)).toEpochMilli());  // 서버 시간을 밀리초(long)로 변환
    //
    //    return calendarDTO;
    //}
    //
    //// contentsName의 모든 특수문자를 _로 변환
    //private String sanitizeContentsName(String contentsName) {
    //    return contentsName.replaceAll("[^a-zA-Z0-9가-힣]", "_");
    //}
}