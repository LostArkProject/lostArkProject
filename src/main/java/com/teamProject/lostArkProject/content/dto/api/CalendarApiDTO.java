package com.teamProject.lostArkProject.content.dto.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CalendarApiDTO {
    @JsonProperty("CategoryName")
    private String categoryName;

    @JsonProperty("ContentsName")
    private String contentsName;

    @JsonProperty("ContentsIcon")
    private String contentsIcon;

    @JsonProperty("MinItemLevel")
    private int minItemLevel;

    @JsonProperty("StartTimes")
    private List<String> startTimes;

    @JsonProperty("Location")
    private String location;

    @JsonProperty("RewardItems")
    private List<RewardItemApiDTO> rewardItems;
}

