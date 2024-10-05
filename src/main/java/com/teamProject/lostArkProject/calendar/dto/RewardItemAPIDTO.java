package com.teamProject.lostArkProject.calendar.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RewardItemAPIDTO {
    @JsonProperty("ItemLevel")
    private int itemLevel;

    @JsonProperty("Items")
    private List<ItemAPIDTO> items;
}
