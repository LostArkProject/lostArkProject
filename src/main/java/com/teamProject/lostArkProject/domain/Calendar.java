package com.teamProject.lostArkProject.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Calendar {
    @JsonProperty("CategoryName")
    private String categoryName;
    @JsonProperty("ContentsName")
    private String contentsName;
    @JsonProperty("ContentsIcon")
    private String contentsIcon;
    @JsonProperty("MinItemLevel")
    private int minItemLevel;
    @JsonProperty("StartTimes")
    private List<LocalDateTime> startTimes;
    @JsonProperty("Location")
    private String location;
    @JsonProperty("RewardItems")
    private List<RewardItem> rewardItems;
    private String remainTime;
}

@Getter
@Setter
class RewardItem {
    @JsonProperty("ItemLevel")
    private int itemLevel;
    @JsonProperty("Items")
    private List<Item> items;
}

@Getter
@Setter
class Item {
    @JsonProperty("Name")
    private String name;
    @JsonProperty("Icon")
    private String icon;
    @JsonProperty("Grade")
    private String grade;
    @JsonProperty("StartTimes")
    private List<LocalDateTime> startTimes;
}
