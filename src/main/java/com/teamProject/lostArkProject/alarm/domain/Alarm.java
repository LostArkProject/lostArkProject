package com.teamProject.lostArkProject.alarm.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("alarm")
public class Alarm {
    private String memberId;
    private String contentName;
}
