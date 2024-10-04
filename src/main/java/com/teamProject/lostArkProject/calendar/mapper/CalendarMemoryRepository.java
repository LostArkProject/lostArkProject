package com.teamProject.lostArkProject.calendar.mapper;

import com.teamProject.lostArkProject.calendar.domain.Calendar;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class CalendarMemoryRepository implements CalendarMapper {
    private List<Calendar> calendars;

    @Override
    public Mono<Void> saveCalendar(List<Calendar> calendar) {
        calendars = calendar;
        return Mono.empty();
    }

    @Override
    public Mono<List<Calendar>> getCalendar() {
        return Mono.just(calendars);
    }
}