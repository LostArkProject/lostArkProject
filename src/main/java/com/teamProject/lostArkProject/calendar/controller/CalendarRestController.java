package com.teamProject.lostArkProject.calendar.controller;

import com.teamProject.lostArkProject.calendar.domain.Calendar;
import com.teamProject.lostArkProject.calendar.dto.CalendarWithTimesDTO;
import com.teamProject.lostArkProject.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CalendarRestController {
    private final CalendarService calendarService;

    // 외부 api에서 데이터를 가져와서 db에 저장
    @GetMapping("/calendar/fetch")
    public Mono<Void> getAndSaveCalendar() {
        return calendarService.getAndSaveCalendar();
    }

    // calendar 데이터의 원본을 반환
    @GetMapping("/calendar/origin")
    public Mono<List<Calendar>> getCalendars() {
        return calendarService.getCalendars();
    }

    // calendar 데이터에서 calendarDTO 객체에 새로운 데이터를 담아서 반환 
    @GetMapping("/calendar")
    public Mono<List<CalendarWithTimesDTO>> getCalendarsWithServerTime() {
       return calendarService.getCalendarWithServerTime();
    }
}
