package com.teamProject.lostArkProject.member.controller;

import com.teamProject.lostArkProject.collectible.domain.CharacterInfo;
import com.teamProject.lostArkProject.collectible.service.CollectibleService;
import com.teamProject.lostArkProject.member.domain.Member;
import com.teamProject.lostArkProject.member.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping ("/member")
public class MemberRestController {
    private final MemberService memberService;
    private final CollectibleService collectibleService;

    public MemberRestController(MemberService memberService, CollectibleService collectibleService) {
        this.memberService = memberService;
        this.collectibleService = collectibleService;
    }

    @PostMapping("/check-email")
    public boolean checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        return memberService.checkEmail(email);
    }

    //대표캐릭터 api 체크
    @PostMapping("/check-representativeCharacter")
    public boolean checkRepresentativeCharacter(@RequestBody Map<String, String> request) {
        String representativeCharacter = request.get("representativeCharacter");
        System.out.println(collectibleService.getCharacterInfo(representativeCharacter));
        return false;
    }

    //회원가입
    @PostMapping("/signup-process")
    public boolean signupProcess(HttpServletRequest request, @RequestBody Map<String, String> requestMap) {
        HttpSession session = request.getSession();
        List<CharacterInfo> characterInfoList = collectibleService.getCharacterInfo(requestMap.get("representativeCharacter"))
                .block();

        if (characterInfoList == null || characterInfoList.isEmpty()) {
            System.out.println("회원가입 실패: 대표 캐릭터가 유효하지 않습니다.");
            return false; // 검증 실패
        }


        Member member = new Member();
        member.setMemberId(requestMap.get("email"));
        member.setMemberPasswd(requestMap.get("PW"));
        member.setRepresentativeCharacterNickname(requestMap.get("representativeCharacter"));

        Member sessionMember = new Member();
        sessionMember.setMemberId(requestMap.get("email"));
        sessionMember.setRepresentativeCharacterNickname(requestMap.get("representativeCharacter"));

        memberService.signupMember(member);
        session.setAttribute("member", sessionMember);
        collectibleService.saveCollectiblePoint(requestMap.get("representativeCharacter"), requestMap.get("email"));
        return true;
    }

    //로그인
    @PostMapping("/signin-process")
    public boolean signinProcess(HttpServletRequest request, @RequestBody Map<String, String> requestMap,
                                 HttpServletResponse response) {
        HttpSession session = request.getSession();
        if (memberService.checkSignin(requestMap.get("email"),requestMap.get("PW"))) {
            String memberNickname = memberService.getRepresentativeCharacterNickname(requestMap.get("email"));

            Member sessionMember = new Member();
            sessionMember.setMemberId(requestMap.get("email"));
            sessionMember.setRepresentativeCharacterNickname(memberNickname);

            session.setAttribute("member", sessionMember);

            //아이디 저장
            if(("true").equals(requestMap.get("saveId"))) {
                Cookie cookie = new Cookie("saveId", requestMap.get("email"));
                response.addCookie(cookie);
            } else {
                Cookie cookie = new Cookie("saveId", null);
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
            return true;
        }
        return false;
    }


}
