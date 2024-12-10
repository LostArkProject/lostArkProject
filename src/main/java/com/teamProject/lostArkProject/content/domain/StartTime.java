package com.teamProject.lostArkProject.content.domain;

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
