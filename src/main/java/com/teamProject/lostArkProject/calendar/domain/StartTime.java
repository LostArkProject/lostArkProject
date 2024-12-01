package com.teamProject.lostArkProject.calendar.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter
@Setter
@Alias("startTime")
public class StartTime {
    private String contentName;
    private LocalDateTime contentStartTime;
}
