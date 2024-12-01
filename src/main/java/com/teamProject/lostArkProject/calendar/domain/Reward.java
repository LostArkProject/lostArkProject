package com.teamProject.lostArkProject.calendar.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Getter
@Setter
@Alias("reward")
public class Reward {
    private String contentName;
    private String rewardItemName;
    private int rewardItemLevel;
    private String rewardItemIconLink;
    private String rewardItemGrade;
}
