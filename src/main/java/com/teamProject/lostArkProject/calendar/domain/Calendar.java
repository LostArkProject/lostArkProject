package com.teamProject.lostArkProject.calendar.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Calendar {
    private long calendarId;
    private String categoryName;
    private String contentsName;
    private String contentsIcon;
    private int minItemLevel;
    private List<StartTime> startTimes;
    private String location;
    private List<RewardItem> rewardItems;
}

