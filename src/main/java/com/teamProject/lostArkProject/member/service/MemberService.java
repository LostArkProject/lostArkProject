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

    public void signupMember(Member member) {
        memberDAO.insertMember(member);
    }

    public boolean checkSignin(String memberId, String insertPW) {
        String DB_PW = memberDAO.signinMember(memberId);
        return DB_PW.equals(insertPW);
    }

    public String getRepresentativeCharacterNickname(String memberId) {
        return memberDAO.getRepresentativeCharacter(memberId);
    }
}
