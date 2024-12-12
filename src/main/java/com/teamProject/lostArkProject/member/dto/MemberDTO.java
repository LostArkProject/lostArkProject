package com.teamProject.lostArkProject.member.dto;

import lombok.Data;

import java.util.Date;

@Data
public class MemberDTO {
    private String member_id;
    private String member_passwd;
    private Date registration_date;
    private String representative_character_nickname;
}
