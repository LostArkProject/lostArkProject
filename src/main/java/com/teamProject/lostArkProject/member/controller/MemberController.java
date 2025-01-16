package com.teamProject.lostArkProject.member.controller;

import com.teamProject.lostArkProject.member.domain.Member;
import com.teamProject.lostArkProject.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입
    @GetMapping("/signup")
    public String signup() {
        return "member/signup";
    }

    // 로그인
    @GetMapping("/signin")
    public String signin(HttpServletRequest request, Model model) {
        String savedId = null;

        // 쿠키에서 "saveId" 값을 읽음
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("saveId".equals(cookie.getName())) {
                    savedId = cookie.getValue();
                    break;
                }
            }
        }

        // 저장된 아이디를 모델에 추가
        model.addAttribute("savedId", savedId);
        model.addAttribute("isSaveIdChecked", savedId != null); // 체크박스 상태
        return "member/signin";
    }

    // 로그아웃
    @GetMapping("/signout")
    public String signout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // 정보 변경
    @GetMapping("/changeInfo")
    public String changeInfo(){

        return "member/changeInfo";
    }
}