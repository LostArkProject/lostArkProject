package com.teamProject.lostArkProject.member.domain;

import lombok.Data;
import org.apache.ibatis.type.Alias;

import java.time.LocalDate;

@Data
@Alias("member")
public class Member {
    private String memberId;
    private String memberPasswd;
    private LocalDate registrationDate;
    private String representativeCharacterNickname;
}
