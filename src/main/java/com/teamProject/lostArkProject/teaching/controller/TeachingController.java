package com.teamProject.lostArkProject.teaching.controller;


import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import com.teamProject.lostArkProject.teaching.service.TeachingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class TeachingController {

    @Autowired
    private TeachingService teachingService;

    @GetMapping("teaching/newMentor")
    public String newMentor(){
        return "teaching/newMentor";
    }

    @PostMapping("/teaching/newMentor")
    public String newMentor(@ModelAttribute MentorDTO mentorDTO) {
        System.out.println(mentorDTO);
        teachingService.newMentor(mentorDTO);
        return "redirect:/index";
    }

    @GetMapping("/teaching/newMentee")
    public String newMentee(){
        return "teaching/newMentee";
    }

    @PostMapping("/teaching/newMentee")
    public String newMentee(@ModelAttribute MenteeDTO menteeDTO) {
        System.out.println(menteeDTO);
        teachingService.newMentee(menteeDTO);
        return "redirect:/index";
    }

    @GetMapping("/teaching/mentorList")
    public String mentorList(){
        return "teaching/mentorList";
    }
}