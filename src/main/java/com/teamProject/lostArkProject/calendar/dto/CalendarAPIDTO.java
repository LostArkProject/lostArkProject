package com.teamProject.lostArkProject.calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CalendarAPIDTO {
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
    private List<RewardItemAPIDTO> rewardItems;
}

