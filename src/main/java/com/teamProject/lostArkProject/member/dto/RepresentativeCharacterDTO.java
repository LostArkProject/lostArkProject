package com.teamProject.lostArkProject.member.dto;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("representativeCharacter")
public class RepresentativeCharacterDTO {
    private String representativeCharacterNickname;
}
