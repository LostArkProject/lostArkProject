package com.teamProject.lostArkProject.teaching.dto;

import lombok.Data;

@Data
public class MenteeApplyDTO {
    private String mentor_member_id;
    private int mentor_content_id;
    private String mentee_member_id;
    private int mentee_content_id;
}
