package com.teamProject.lostArkProject.teaching.controller;


import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import com.teamProject.lostArkProject.teaching.service.TeachingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
public class TeachingController {

    @Autowired
    private TeachingService teachingService;

    @GetMapping("/teaching/newMentor")
    public String newMentor(HttpSession session) {
        // 세션에서 "memberNickname" 값 확인
        String memberNickname = (String) session.getAttribute("memberNickname");

        if (memberNickname == null) {
            // 세션에 "memberNickname"이 없으면 접근 불가
            return "redirect:/member/signin"; // 로그인 페이지로 리다이렉트
        }
        // "memberNickname"이 존재하면 페이지 반환
        return "teaching/newMentor";
    }

    @PostMapping("/teaching/newMentor")
    public String newMentor(@ModelAttribute MentorDTO mentorDTO) {
        System.out.println("recieved : " + mentorDTO);
        teachingService.newMentor(mentorDTO);
        return "redirect:/teaching/mentorList";
    }
    /*
    @GetMapping("/teaching/newMentee")
    public String newMentee(){
        return "teaching/newMentee";
    }

    @PostMapping("/teaching/newMentee")
    public String newMentee(@ModelAttribute MenteeDTO menteeDTO) {
        System.out.println(menteeDTO);
        teachingService.newMentee(menteeDTO);
        return "redirect:/index";
    }*/

    @GetMapping("/teaching/mentorList")
    public String mentorList(HttpSession session, Model model) {
        // 세션에서 "memberNickname" 값을 확인
        String memberNickname = (String) session.getAttribute("memberNickname");
        // 세션에 값이 없으면 로그인 페이지로 리다이렉트
        if (memberNickname == null) {
            return "redirect:/member/signin"; // 로그인 페이지로 리다이렉트
        }

        // 세션 값이 존재하면 서비스 호출 및 데이터 처리
        List<Map<String, Object>> mentorList = teachingService.getMentorList();
        model.addAttribute("mentorMap", mentorList);
        return "teaching/mentorList";
    }

    @GetMapping("/teaching/mentorListDetail")
    public String mentorListDetail() { return "teaching/mentorListDetail";}

}