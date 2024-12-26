package com.teamProject.lostArkProject.content.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter
@Setter
@Alias("startTimeDTO")
public class StartTimeDTO {
    private int contentId;
    private LocalDateTime contentStartTime;
}
