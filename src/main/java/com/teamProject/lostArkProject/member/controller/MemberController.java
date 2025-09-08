package com.teamProject.lostArkProject.member.controller;

import com.teamProject.lostArkProject.collectible.domain.CharacterInfo;
import com.teamProject.lostArkProject.member.domain.Member;
import com.teamProject.lostArkProject.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

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

    //비밀번호 찾기
    @GetMapping("/findPassword")
    public String findPasswrod() { return "member/findPassword"; }

    // 로그아웃
    @GetMapping("/signout")
    public String signout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    // 정보 변경
    @GetMapping("/myPage")
    public String myPage(HttpServletRequest request){
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("member");
        if(member == null) {
            return "index";
        }
        return "member/myPage";
    }

    // 캐릭터 인증 페이지
    @GetMapping("/certification")
    public String certification(HttpServletRequest request, Model model){
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("member");
        if(member == null) {
            return "index";
        }
        List<CharacterInfo> characterInfoList = memberService.getCharacterInfo(member.getRepresentativeCharacterNickname())
                .block();

        model.addAttribute("characterInfoList", characterInfoList);
        return "member/certification";
    }
}
