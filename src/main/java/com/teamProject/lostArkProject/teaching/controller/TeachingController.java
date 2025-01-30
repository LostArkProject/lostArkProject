package com.teamProject.lostArkProject.teaching.controller;


import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorListDTO;
import com.teamProject.lostArkProject.teaching.service.TeachingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class TeachingController {

    @Autowired
    private TeachingService teachingService;

    @GetMapping("/teaching/newMentor")
    public String newMentor(HttpSession session) {
        // 세션에서 "member" 객체 확인
        Object member = session.getAttribute("member");

        if (member == null) {
            // 세션에 "member" 객체가 없으면 접근 불가
            return "redirect:/member/signin"; // 로그인 페이지로 리다이렉트
        }
        // "member" 객체가 존재하면 페이지 반환
        return "teaching/newMentor";
    }


    @PostMapping("/teaching/newMentor")
    public String newMentor(@ModelAttribute MentorDTO mentorDTO, @RequestParam("mentorContentId[]") String[] contentIds) {
        // 배열로 받은 mentorContentId를 하나의 문자열로 변환
        String joinedContentIds = String.join(", ", contentIds);
        // DTO에 문자열로 저장
        mentorDTO.setMentorContentId(joinedContentIds);
        // 서비스 레이어를 통해 데이터베이스에 저장
        teachingService.newMentor(mentorDTO);
        return "redirect:/teaching/mentorList";
    }

    @GetMapping("/teaching/mentorList")
    public String mentorList(HttpSession session, Model model) {
        Object member = session.getAttribute("member");
        if (member == null) {
            // 세션에 "member" 객체가 없으면 접근 불가
            return "redirect:/member/signin"; // 로그인 페이지로 리다이렉트
        }
        List<MentorListDTO> mentors = teachingService.getMentorList();
        model.addAttribute("mentors", mentors);
        return "teaching/mentorList";
    }

    @GetMapping("/teaching/mentorListDetail/{mentorMemberId}")
    public String mentorListDetail(@PathVariable("mentorMemberId") String mentorMemberId, Model model) {
        List<MentorListDTO> mentors = teachingService.getMentorDetail(mentorMemberId);
        model.addAttribute("mentors", mentors);

        return "teaching/mentorListDetail";
    }
}