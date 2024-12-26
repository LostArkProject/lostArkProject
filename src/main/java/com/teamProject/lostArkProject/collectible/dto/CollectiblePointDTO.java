package com.teamProject.lostArkProject.collectible.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectiblePointDTO {
    @JsonProperty("Type")
    private String collectibleTypeName;
    @JsonProperty("MemberId")
    private String memberId;
    @JsonProperty("Icon")
    private String collectibleIconLink;
    @JsonProperty("CollectiblePoints")
    private List<CollectiblePointDetailDTO> collectiblePoints;
}

