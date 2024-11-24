package com.teamProject.lostArkProject.member.dto;

import lombok.Data;

@Data
public class MemberCharacterDTO {
    private String character_nickname;
    private String server_name;
    private String character_class;
    private int item_level;
    private int roster_level;
    private String member_id;
}
