package com.teamProject.lostArkProject.content.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Getter
@Setter
@Alias("content")
public class Content {
    private int id;
    private String contentName;
    private String contentIconLink;
    private int minItemLevel;
    private String contentLocation;
    private String contentCategory;

    private List<StartTime> startTimes;
    private List<Reward> rewards;
}

