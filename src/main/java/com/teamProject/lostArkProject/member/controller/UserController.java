package com.teamProject.lostArkProject.member.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/user/signup")
    public String signup(HttpSession session, Model model){
        Object user = session.getAttribute("user");
        if (user != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", user.toString()); // user 객체에 따라 다를 수 있음
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "user/signup";
    }
    @GetMapping("/user/signin")
    public String signin(HttpSession session, Model model){
        Object user = session.getAttribute("user");
        if (user != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", user.toString()); // user 객체에 따라 다를 수 있음
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "user/signin";
    }

    @GetMapping("/user/changeinfo")
    public String changeInfo(){

        return "user/changeinfo";
    }


}
