package com.teamProject.lostArkProject.collectible.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "내실 Entity")
public class CollectiblePoint {
    @JsonProperty("Type")
    @Schema(description = "", example = "")
    private String collectibleTypeName;

    @JsonProperty("PointName")
    @Schema(description = "", example = "")
    private String collectiblePointName;

    @JsonProperty("MemberId")
    @Schema(description = "", example = "")
    private String memberId;

    @JsonProperty("Point")
    @Schema(description = "", example = "")
    private int collectedPoint;

    @JsonProperty("MaxPoint")
    @Schema(description = "", example = "")
    private int collectibleMaxPoint;

    @JsonProperty("Icon")
    @Schema(description = "", example = "")
    private String collectibleIconLink;
}