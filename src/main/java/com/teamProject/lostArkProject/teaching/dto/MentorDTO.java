package com.teamProject.lostArkProject.teaching.dto;

import lombok.Data;

import java.util.List;

@Data
public class MentorDTO {
    private String mentorMemberId;
    private String mentorDiscordId;
    private String mentorWantToSay;
    private String mentorContentId; // 이제 이 필드는 문자열입니다.

    // getters and setters
}
