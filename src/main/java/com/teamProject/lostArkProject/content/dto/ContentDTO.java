package com.teamProject.lostArkProject.content.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Getter
@Setter
@Alias("contentDTO")
public class ContentDTO {
    private int contentId;
    private String contentName;
    private String contentIconLink;
    private int minItemLevel;
    private String contentLocation;
    private String contentCategory;

    private List<StartTimeDTO> startTimes;
    private List<RewardDTO> rewards;
}
