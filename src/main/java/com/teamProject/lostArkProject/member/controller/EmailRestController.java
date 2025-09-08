package com.teamProject.lostArkProject.member.controller;

import com.teamProject.lostArkProject.member.service.EmailService;
import com.teamProject.lostArkProject.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Tag(name = "이메일 RestAPI", description = "EmailRestController")
public class EmailRestController {

    private final EmailService emailService;
    private final MemberService memberService;

    @ResponseBody
    @PostMapping("/send-email")
    @Operation(summary = "인증번호 전송", description = "이메일 인증을 위해 해당 이메일에 인증번호를 전송합니다.")
    public void emailCheck(HttpServletRequest request, @RequestBody Map<String, String> requestMap) throws MessagingException, UnsupportedEncodingException {
        String authCode = emailService.sendSimpleMessage(requestMap.get("email"));
        emailService.deleteAuthCode(requestMap.get("email"));
        emailService.saveAuthCode(requestMap.get("email"), authCode);

        HttpSession session = request.getSession();
        session.setAttribute("checkCode", authCode);
        session.setMaxInactiveInterval(600); // 10 * 60 = 10분
    }
}