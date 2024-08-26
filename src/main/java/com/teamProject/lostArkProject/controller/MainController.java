package com.teamProject.lostArkProject.controller;

import com.teamProject.lostArkProject.domain.CharacterInfo;
import com.teamProject.lostArkProject.service.LostArkAPIService;
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

    /* 지금은 간단하게 뷰를 이동해서 데이터를 출력하는 방식으로 구현해뒀는데
     * 이후에 실제로 api를 사용할 때는 뷰 이동 방식이 아니라 데이터를 반환하는 방식으로 구현해야 할 것 같음
     */
    @GetMapping("/characters")
    public Mono<String> getCharacterInfo(@RequestParam String characterName, Model model) {
        Mono<List<CharacterInfo>> characterInfoMono = lostArkAPIService.getCharacterInfo(characterName);
        return characterInfoMono.flatMap(characterInfoList -> {
            model.addAttribute("characterList", characterInfoList);
            return Mono.just("characters"); // 결과 뷰로 이동
        });
    }
}