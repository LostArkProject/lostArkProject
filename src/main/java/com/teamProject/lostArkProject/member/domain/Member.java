package com.teamProject.lostArkProject.member.domain;

import lombok.*;
import org.apache.ibatis.type.Alias;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Alias("member")
public class Member {
    private String memberId;
    private String memberPasswd;
    private LocalDate registrationDate;
    private String representativeCharacterNickname;
}
