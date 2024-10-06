package com.teamProject.lostArkProject.calendar.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    private long itemId;
    private long rewardItemId;
    private String name;
    private String icon;
    private String grade;
}
