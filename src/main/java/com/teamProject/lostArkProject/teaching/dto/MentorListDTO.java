package com.teamProject.lostArkProject.teaching.dto;

import lombok.Data;

import java.util.List;

@Data
public class MentorListDTO {
    private String mentorMemberId;
    private String mentorWantToSay;
    private List<String> mentorContentIds; // 변경된 부분
    private String characterNickname;
    private String itemLevel;
    private String serverName;
    // Getter & Setter
}

