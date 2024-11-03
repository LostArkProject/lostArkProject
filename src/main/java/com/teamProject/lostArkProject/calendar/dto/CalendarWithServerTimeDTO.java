package com.teamProject.lostArkProject.calendar.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CalendarWithServerTimeDTO {
    private String categoryName;
    private String contentsName;
    private String sanitizedContentsName;
    private String contentsIcon;
    private int minItemLevel;
    private List<LocalDateTime> startTimes;
    private LocalDateTime serverTime;
    private String location;
    private List<ItemDTO> items;
}