package com.teamProject.lostArkProject.calendar.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CalendarWithTimesDTO {
    private String categoryName;
    private String contentsName;
    private String sanitizedContentsName;
    private String contentsIcon;
    private int minItemLevel;
    private long serverTime;
    private long remainTime;
}