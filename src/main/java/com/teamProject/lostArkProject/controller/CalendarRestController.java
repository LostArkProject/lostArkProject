package com.teamProject.lostArkProject.controller;

import com.teamProject.lostArkProject.domain.Calendar;
import com.teamProject.lostArkProject.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CalendarRestController {
    private final CalendarService calendarService;

    @GetMapping("/cal")
    public Mono<List<Calendar>> updateCalendar() {
        return calendarService.getCalendar();
    }
}
