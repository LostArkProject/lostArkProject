package com.teamProject.lostArkProject.dto;

import lombok.Getter;
import lombok.Setter;

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
    private LocalDateTime remainTime;
    private String location;
    private List<Item> items;
}

@Getter
@Setter
class Item {
    private String name;
    private String icon;
    private String grade;
}
