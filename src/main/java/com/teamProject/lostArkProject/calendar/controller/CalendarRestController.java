package com.teamProject.lostArkProject.calendar.controller;

import com.teamProject.lostArkProject.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class CalendarRestController {
    private final CalendarService calendarService;

    // 외부 api에서 데이터를 가져와서 db에 저장
    @GetMapping("/content/fetch")
    public ResponseEntity<Mono<String>> getAndSaveContent() {
        return ResponseEntity.ok(calendarService.saveContent());
    }

    //// calendar 데이터의 원본을 반환
    //@GetMapping("/calendar/origin")
    //public Mono<List<Calendar>> getCalendars() {
    //    return calendarService.getCalendars();
    //}
    //
    //// calendar 데이터에서 calendarDTO 객체에 새로운 데이터를 담아서 반환
    //@GetMapping("/calendar")
    //public Mono<List<CalendarWithServerTimeDTO>> getCalendarsWithServerTime() {
    //   return calendarService.getCalendarWithServerTime();
    //}
}
