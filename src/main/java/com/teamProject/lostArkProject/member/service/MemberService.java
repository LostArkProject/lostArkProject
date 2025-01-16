package com.teamProject.lostArkProject.member.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamProject.lostArkProject.member.dao.MemberDAO;
import com.teamProject.lostArkProject.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MemberService {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final MemberDAO memberDAO;

    public MemberService(WebClient webClient, ObjectMapper objectMapper, MemberDAO memberDAO) {
        this.webClient = webClient;
        this.objectMapper = objectMapper;
        this.memberDAO = memberDAO;
    }

    //회원가입 로직
    public void signupMember(Member member) {
        memberDAO.insertMember(member);
    }

    //로그인 비밀번호 확인
    public boolean checkSignin(String memberId, String insertPW) {
        String DB_PW = memberDAO.getMemberPW(memberId);
        return DB_PW.equals(insertPW);
    }

    //대표 캐릭터 닉네임 가져오기
    public String getRepresentativeCharacterNickname(String memberId) {
        return memberDAO.getRepresentativeCharacter(memberId);
    }

    //이메일 체크
    public boolean checkEmail(String memberId) {
        if(memberDAO.checkMemberId(memberId)==null) return false;
        return true;
    }
}
