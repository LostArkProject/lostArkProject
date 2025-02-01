package com.teamProject.lostArkProject.content.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.util.List;

@Getter
@Setter
@Alias("contentStartTimeDTO")
public class ContentStartTimeDTO {
    private int contentNumber;
    private String contentName;
    private String contentIconLink;
    private String contentCategory;

    private List<StartTimeDTO> contentStartTimes;
}
