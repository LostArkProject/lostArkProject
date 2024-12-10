package com.teamProject.lostArkProject.content.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RewardItemApiDTO {
    @JsonProperty("ItemLevel")
    private int itemLevel;

    @JsonProperty("Items")
    private List<ItemApiDTO> items;
}
