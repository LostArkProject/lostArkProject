package com.teamProject.lostArkProject.calendar.dto;

import com.teamProject.lostArkProject.calendar.domain.StartTime;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CalendarDTO {
    private String categoryName;
    private String contentsName;
    private String sanitizedContentsName;
    private String contentsIcon;
    private int minItemLevel;
    private List<StartTime> startTimes;
    private LocalDateTime serverTime;
    private String location;
    private List<ItemDTO> items;
}