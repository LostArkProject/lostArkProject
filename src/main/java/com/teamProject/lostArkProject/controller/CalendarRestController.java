package com.teamProject.lostArkProject.controller;

import com.teamProject.lostArkProject.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CalendarRestController {
    private final CalendarService calendarService;
}
