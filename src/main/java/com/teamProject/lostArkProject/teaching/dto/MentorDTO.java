package com.teamProject.lostArkProject.teaching.dto;

import lombok.Data;

import java.util.List;

@Data
public class MentorDTO {
    private String mentorMemberId;
    private List<Integer> mentorContentId;
    private String mentorWantToSay;
    private String mentorDiscordId;
}
