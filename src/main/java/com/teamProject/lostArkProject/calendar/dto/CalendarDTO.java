package com.teamProject.lostArkProject.calendar.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CalendarDTO {
    private String categoryName;
    private String contentsName;
    private String contentsIcon;
    private int minItemLevel;
    private List<LocalDateTime> startTimes;
    //private Duration remainTime;
    private LocalDateTime serverTime;
    private String location;
    private List<ItemDTO> items;
}