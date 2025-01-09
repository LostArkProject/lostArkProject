package com.teamProject.lostArkProject.member.dao;

import com.teamProject.lostArkProject.member.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDAO {
    void insertMember(Member member);
    String signinMember(String memberId);
    String getRepresentativeCharacter(String memberId);
}
