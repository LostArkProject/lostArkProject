package com.teamProject.lostArkProject.member.controller;

import com.teamProject.lostArkProject.member.domain.Member;
import com.teamProject.lostArkProject.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원가입
    @GetMapping("/member/signup")
    public String signup() {
        return "member/signup";
    }

    @PostMapping("/member/signup/process")
    public String signupProcess(HttpServletRequest request, Model model, @RequestParam String signupId,
                         @RequestParam String signupPW, @RequestParam String signupRepresentativeCharacter) {
        HttpSession session = request.getSession();
        Member member = new Member();
        member.setMemberId(signupId);
        member.setMemberPasswd(signupPW);
        member.setRepresentativeCharacterNickname(signupRepresentativeCharacter);

        memberService.signupMember(member);
        session.setAttribute("memberNickname", signupRepresentativeCharacter);
        model.addAttribute("memberId", signupId);
        return "index";
    }

    // 로그인
    @GetMapping("/member/signin")
    public String signin() {
        return "member/signin";
    }

    @PostMapping("/member/signin/process")
    public String signinProcess(HttpServletRequest request, Model model, @RequestParam String signinId,
                                @RequestParam String signinPW) {
        HttpSession session = request.getSession();
        if (memberService.checkSignin(signinId,signinPW)) {
            String memberNickname = memberService.getRepresentativeCharacterNickname(signinId);
            session.setAttribute("memberNickname", memberNickname);
            return "index";
        }
        System.out.println("<script>alert('프로필이 수정되었습니다.');</script>");
        return "member/signin";
    }

    // 로그아웃
    @GetMapping("/member/signout")
    public String signout(HttpSession session) {
        session.invalidate();
        return "index";
    }

    // 정보 변경
    @GetMapping("/member/changeInfo")
    public String changeInfo(){

        return "member/changeInfo";
    }
}