package com.teamProject.lostArkProject.member.controller;

import com.teamProject.lostArkProject.member.dto.EmailDTO;
import com.teamProject.lostArkProject.member.service.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @ResponseBody
    @PostMapping("/send-email")
    public void emailCheck(@RequestBody Map<String, String> requestMap) throws MessagingException, UnsupportedEncodingException {
        String authCode = emailService.sendSimpleMessage(requestMap.get("email"));
    }
}