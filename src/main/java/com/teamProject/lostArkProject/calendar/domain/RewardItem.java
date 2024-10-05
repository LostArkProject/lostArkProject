package com.teamProject.lostArkProject.calendar.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RewardItem {
    private long id;
    private long calendarId;
    private int itemLevel;
    private List<Item> items;
}
