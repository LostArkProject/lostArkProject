package com.teamProject.lostArkProject.common.controller;

import com.teamProject.lostArkProject.member.domain.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/** 
 * 세션의 사용자 정보를 자동으로 전송하는 전역 controller
 * member 데이터는 매 요청마다 "loggedInMember"의 네이밍으로 model에 추가
 */
@ControllerAdvice
public class GlobalUserAdvice {
    @ModelAttribute("loggedInMember")
    public Member addLoggedInMemberToModel(HttpSession session) {
        return (Member) session.getAttribute("member");
    }
}
