package com.teamProject.lostArkProject.collectible.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 캐릭터 Entity")
public class CharacterInfo {
    @JsonProperty("ServerName")
    @Schema(description = "서버이름", example = "카제로스")
    private String serverName;

    @JsonProperty("CharacterName")
    @Schema(description = "캐릭터 이름", example = "GCoca")
    private String characterName;

    @JsonProperty("CharacterLevel")
    @Schema(description = "캐릭터 레벨", example = "70")
    private int characterLevel;

    @JsonProperty("CharacterClassName")
    @Schema(description = "캐릭터 클래스 이름", example = "데모닉")
    private String characterClassName;

    @JsonProperty("ItemAvgLevel")
    @Schema(description = "평균 아이템 레벨", example = "1670")
    private String itemAvgLevel;

    @JsonProperty("ItemMaxLevel")
    @Schema(description = "최고 아이템 레벨", example = "1670")
    private String itemMaxLevel;
}
