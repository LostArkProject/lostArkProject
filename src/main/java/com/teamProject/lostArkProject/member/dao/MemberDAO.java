package com.teamProject.lostArkProject.member.dao;

import com.teamProject.lostArkProject.member.domain.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDAO {
    void insertMember(Member member);
    String getMemberPW(String memberId);
    String getRepresentativeCharacter(String memberId);
    String checkMemberId(String memberId);
}
