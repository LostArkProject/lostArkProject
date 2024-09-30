package com.teamProject.lostArkProject.repository;

import com.teamProject.lostArkProject.domain.Calendar;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface CalendarRepository {
    void saveCalendar(List<Calendar> calendar);
    Mono<List<Calendar>> getCalendar();
}
