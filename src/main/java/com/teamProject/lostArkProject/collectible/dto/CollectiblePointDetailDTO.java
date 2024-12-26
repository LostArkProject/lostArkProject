package com.teamProject.lostArkProject.collectible.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectiblePointDetailDTO {
    @JsonProperty("PointName")
    private String collectiblePointName;

    @JsonProperty("Point")
    private int collectedPoint;

    @JsonProperty("MaxPoint")
    private int collectibleMaxPoint;
}
