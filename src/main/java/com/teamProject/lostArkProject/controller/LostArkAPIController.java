package com.teamProject.lostArkProject.controller;

import com.teamProject.lostArkProject.domain.Calendar;
import com.teamProject.lostArkProject.domain.CharacterInfo;
import com.teamProject.lostArkProject.domain.CollectibleItem;
import com.teamProject.lostArkProject.service.LostArkAPIService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
public class LostArkAPIController {

    private final LostArkAPIService lostArkAPIService;

    public LostArkAPIController(LostArkAPIService LostArkAPIService) {
        this.lostArkAPIService = LostArkAPIService;
    }

    @GetMapping("/characters/{characterName}/siblings")
    public Mono<List<CharacterInfo>> getCharacterInfo(@PathVariable String characterName) {
        return lostArkAPIService.getCharacterInfo(characterName);
    }

    @GetMapping("/remaintimes")
    public Mono<Map<String, Calendar>> getRemainTimes() {
        return lostArkAPIService.getCalendar()
                .flatMap(calendar -> lostArkAPIService.getRemainTimes());
    }

    // collectibles url로 GET 요청 시 json 데이터 반환
    @GetMapping("/collectibles")
    public Mono<List<CollectibleItem>> getCharacterCollectable(HttpSession session) {
        String characterName = (String) session.getAttribute("nickname"); // http 세션에서 가져온 닉네임
        return lostArkAPIService.getCharacterCollectible(characterName);
    }
}