package com.teamProject.lostArkProject.teaching.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeachingController {

    @GetMapping("/teaching/newMentor")
    public String newMentor() {
        return "teaching/newMentor";
    }

    @GetMapping("/teaching/newMentee")
    public String newMentee(){
        return "teaching/newMentee";
    }

    @GetMapping("/teaching/mentorList")
    public String mentorList(){
        return "teaching/mentorList";
    }
}