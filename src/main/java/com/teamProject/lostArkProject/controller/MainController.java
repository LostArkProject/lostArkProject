package com.teamProject.lostArkProject.controller;


import org.springframework.web.bind.annotation.GetMapping;

public class MainController {

    @GetMapping("/main")
    public String main(){
        return "main";
    }
}
