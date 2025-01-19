package com.teamProject.lostArkProject.member.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("checkEmail")
public class MemberEmailCheckDTO {
    private String memberId;
}
