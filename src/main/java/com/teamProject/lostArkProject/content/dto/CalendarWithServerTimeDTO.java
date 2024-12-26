package com.teamProject.lostArkProject.content.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CalendarWithServerTimeDTO {
    private String categoryName;
    private String contentsName;
    private String sanitizedContentsName;
    private String contentsIcon;
    private int minItemLevel;
    private List<Long> startTimes;
    private long serverTime;
}