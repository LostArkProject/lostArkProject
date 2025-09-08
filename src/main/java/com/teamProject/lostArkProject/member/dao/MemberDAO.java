package com.teamProject.lostArkProject.member.dao;

import com.teamProject.lostArkProject.member.domain.Member;
import com.teamProject.lostArkProject.member.domain.MemberCharacter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MemberDAO {
    //데이터 저장
    void insertMember(Member member);
    void insertMemberCharacter(MemberCharacter memberCharacter);
    void insertAuthCode(String memberId, String authCode);

    //데이터 조회
    String getMemberPW(String memberId);
    String getRepresentativeCharacter(String memberId);
    String checkMemberId(String memberId);
    String getAuthCode(String memberId);
    void deleteAuthCode(String memberId);

    //데이터 삭제
    void deleteMemberCharacter(String memberId);

    //데이터 변경
    void updateRCN(String memberId, String RCN);
    void updateMemberPW(String memberId, String memberPW);
    void updateCertification(String memberId);
}
