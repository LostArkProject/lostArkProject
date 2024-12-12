package com.teamProject.lostArkProject.content.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Alias("rewardDTO")
public class RewardDTO {
    private int rewardId;
    private int contentId;
    private String rewardItemName;
    private int rewardItemLevel;
    private String rewardItemIconLink;
    private String rewardItemGrade;
}
