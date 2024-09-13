package com.teamProject.lostArkProject.controller;

import com.teamProject.lostArkProject.domain.CharacterInfo;
import com.teamProject.lostArkProject.domain.CollectibleItem;
import com.teamProject.lostArkProject.service.LostArkAPIService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
    private final LostArkAPIService lostArkAPIService;

    public MainController(LostArkAPIService lostArkAPIService) {
        this.lostArkAPIService = lostArkAPIService;
    }

    @GetMapping("/")
    public String character(Model model) {
        model.addAttribute("characterList", new ArrayList<CharacterInfo>());
        return "index";
    }
    @GetMapping("/main")
    public String main(HttpSession session, Model model) {
        Object user = session.getAttribute("user");
        if (user != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", user.toString()); // user 객체에 따라 다를 수 있음
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "main"; // 렌더링할 템플릿 이름
    }

    @GetMapping("/signup")
    public String signup(HttpSession session, Model model){
        Object user = session.getAttribute("user");
        if (user != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", user.toString()); // user 객체에 따라 다를 수 있음
        } else {
            model.addAttribute("isLoggedIn", false);
        }
        return "project/signup";
    }

    @GetMapping("/characters")
    public Mono<String> getCharacterInfo(@RequestParam String characterName, Model model,
                                         HttpServletRequest request) {
        // 클라이언트가 입력한 캐릭터 닉네임을 http 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("nickname", characterName);
        Mono<List<CharacterInfo>> characterInfoMono = lostArkAPIService.getCharacterInfo(characterName);
        return characterInfoMono.flatMap(characterInfoList -> {
            model.addAttribute("characterList", characterInfoList);
            return Mono.just("characters");
        });
    }

    // 캘린더
    @GetMapping("/calendar")
    public Mono<String> calendar(Model model) {
        return Mono.zip(
                lostArkAPIService.getCalendar(),
                lostArkAPIService.getRemainTimes()
        ).doOnNext(tuple -> {
            model.addAttribute("calendar", tuple.getT1());
            model.addAttribute("remainTimes", tuple.getT2());
        }).then(Mono.just("calendar"));
    }

    @GetMapping("/index")
    public Mono<String> home(Model model) {
        return Mono.zip(
                lostArkAPIService.getCalendar(),
                lostArkAPIService.getRemainTimes()
        ).doOnNext(tuple -> {
            model.addAttribute("calendar", tuple.getT1());
            model.addAttribute("remainTimes", tuple.getT2());
        }).then(Mono.just("project/index"));
    }

    // 내실
    @GetMapping("/collectible")
    public Mono<String> getCharacterCollectable(Model model, HttpSession session) {
        String characterName = (String) session.getAttribute("nickname"); // http 세션에서 가져온 닉네임
        Mono<List<CollectibleItem>> collectibleItemMono = lostArkAPIService.getCharacterCollectible(characterName);
        return collectibleItemMono.flatMap(collectibleItemList -> {
            model.addAttribute("collectibleItemList", collectibleItemList);
            return Mono.just("project/collectible"); // 결과 뷰로 이동
        });
    }
}