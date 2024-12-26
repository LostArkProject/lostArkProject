package com.teamProject.lostArkProject.collectible.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectiblePointSummaryDTO {
    private String collectibleTypeName;
    private String collectibleIconLink;
    private int totalCollectedTypePoint;
    private int totalCollectibleTypePoint;
}
