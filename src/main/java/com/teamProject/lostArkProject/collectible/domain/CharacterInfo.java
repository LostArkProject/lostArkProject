package com.teamProject.lostArkProject.collectible.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CharacterInfo {
    @JsonProperty("ServerName")
    private String serverName;
    @JsonProperty("CharacterName")
    private String characterName;
    @JsonProperty("CharacterLevel")
    private int characterLevel;
    @JsonProperty("CharacterClassName")
    private String characterClassName;
    @JsonProperty("ItemAvgLevel")
    private String itemAvgLevel;
    @JsonProperty("ItemMaxLevel")
    private String itemMaxLevel;
}
