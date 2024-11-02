package com.teamProject.lostArkProject.teaching.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TeachingController {

    @GetMapping("/teaching/newMentor")
    public String newMentor(){
        return "project/newMentor";
    }




}
