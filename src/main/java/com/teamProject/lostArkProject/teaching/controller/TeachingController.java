package com.teamProject.lostArkProject.teaching.controller;


import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorListDTO;
import com.teamProject.lostArkProject.teaching.service.TeachingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        //String memberNickname = (String) session.getAttribute("memberNickname");
        // 세션에 값이 없으면 로그인 페이지로 리다이렉트
       // if (memberNickname == null) {
        //    return "redirect:/member/signin"; // 로그인 페이지로 리다이렉트
       // }

        // 세션 값이 존재하면 서비스 호출 및 데이터 처리
        List<MentorListDTO> mentors = teachingService.getMentorList();
        model.addAttribute("mentors", mentors);
        return "teaching/mentorList";
    }

    @GetMapping("/teaching/mentorListDetail")
    public String mentorListDetail() { return "teaching/mentorListDetail";}

}