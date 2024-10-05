package com.teamProject.lostArkProject.calendar.mapper;

import com.teamProject.lostArkProject.calendar.domain.Calendar;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface CalendarMapper {
    int insertCalendar(List<Calendar> calendar);
    List<Calendar> selectCalendar();
}
