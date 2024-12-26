package com.teamProject.lostArkProject.collectible.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CollectiblePoint {
    @JsonProperty("Type")
    private String collectibleTypeName;
    @JsonProperty("PointName")
    private String collectiblePointName;
    @JsonProperty("MemberId")
    private String memberId;
    @JsonProperty("Point")
    private int collectedPoint;
    @JsonProperty("MaxPoint")
    private int collectibleMaxPoint;
    @JsonProperty("Icon")
    private String collectibleIconLink;
}