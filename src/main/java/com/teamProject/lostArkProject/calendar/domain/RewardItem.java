package com.teamProject.lostArkProject.calendar.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RewardItem {
    @JsonProperty("ItemLevel")
    private int itemLevel;
    @JsonProperty("Items")
    private List<Item> items;
}
