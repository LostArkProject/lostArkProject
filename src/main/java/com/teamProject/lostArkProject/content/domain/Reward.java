package com.teamProject.lostArkProject.content.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

@Getter
@Setter
@Alias("reward")
public class Reward {
    private int rewardId;
    private int contentId;
    private String rewardItemName;
    private int rewardItemLevel;
    private String rewardItemIconLink;
    private String rewardItemGrade;

    // 테스트 코드를 간결하게 작성하기 위한 생성자
    public Reward(int contentId, String rewardItemName, int rewardItemLevel, String rewardItemIconLink, String rewardItemGrade) {
        this.contentId = contentId;
        this.rewardItemName = rewardItemName;
        this.rewardItemLevel = rewardItemLevel;
        this.rewardItemIconLink = rewardItemIconLink;
        this.rewardItemGrade = rewardItemGrade;
    }

    // 데이터 매핑 시 빈 객체를 생성하기 위한 기본 생성자
    public Reward() {

    }
}
