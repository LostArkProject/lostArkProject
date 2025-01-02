package com.teamProject.lostArkProject.member.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("signinMember")
public class MemberSigninDTO {
    private String member_passwd;
}
