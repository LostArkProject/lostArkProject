package com.teamProject.lostArkProject.collectible.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "추천 내실 Entity")
@Alias("recommend_collectible")
public class RecommendCollectible {
    private String memberId;
    private int recommendCollectibleID;
    private String recommendCollectibleName;
    private String recommendCollectibleURL;
}
