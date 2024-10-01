package com.teamProject.lostArkProject.calendar.controller;

import com.teamProject.lostArkProject.calendar.domain.Calendar;
import com.teamProject.lostArkProject.calendar.dto.CalendarDTO;
import com.teamProject.lostArkProject.calendar.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CalendarRestController {
    private final CalendarService calendarService;

    @GetMapping("/cal")
    public Mono<Void> getAndSaveCalendar() {
        return calendarService.getAndSaveCalendar();
    }

    @GetMapping("/getcal")
    public Mono<List<Calendar>> getCalendars() {
        return calendarService.getCalendars();
    }

    @GetMapping("/getcal-with-remaintime")
    public Mono<List<CalendarDTO>> getCalendarDTOList() {
       return calendarService.getCalendarWithRemainTime();
    }

    //@GetMapping("/remaintimes")
    //public Mono<Map<String, Calendar>> getRemainTimes() {
    //    return lostArkAPIService.getCalendar()
    //            .flatMap(calendar -> lostArkAPIService.getRemainTimes());
    //}
}
