package com.teamProject.lostArkProject.member.controller;

import com.teamProject.lostArkProject.member.domain.Member;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberController {

    // 회원가입
    @GetMapping("/member/signup")
    public String signup(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("member");
        model.addAttribute("member", member);
        return "member/signup";
    }
    
    // 로그인
    @GetMapping("/member/signin")
    public String signin(HttpSession session, Model model) {
        Member member = (Member) session.getAttribute("member");
        model.addAttribute("member", member);
        return "member/signin";
    }

    // 정보 변경
    @GetMapping("/member/changeInfo")
    public String changeInfo(){

        return "member/changeInfo";
    }
}