package com.teamProject.lostArkProject.member.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/member/signup")
    public String signup(HttpSession session, Model model){
        Object member = session.getAttribute("member");
        if (member != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", member.toString()); // member 객체에 따라 다를 수 있음
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "member/signup";
    }
    @GetMapping("/member/signin")
    public String signin(HttpSession session, Model model){
        Object member = session.getAttribute("member");
        if (member != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", member.toString()); // member 객체에 따라 다를 수 있음
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "member/signin";
    }

    @GetMapping("/member/changeinfo")
    public String changeInfo(){

        return "member/changeinfo";
    }


}
