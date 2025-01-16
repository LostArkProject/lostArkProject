package com.teamProject.lostArkProject.main.controller;

import com.teamProject.lostArkProject.content.service.ContentService;
import com.teamProject.lostArkProject.collectible.domain.CharacterInfo;
import com.teamProject.lostArkProject.collectible.domain.CollectiblePoint;
import com.teamProject.lostArkProject.collectible.dto.CollectiblePointDTO;
import com.teamProject.lostArkProject.collectible.dto.CollectiblePointSummaryDTO;
import com.teamProject.lostArkProject.collectible.service.CollectibleService;
import com.teamProject.lostArkProject.member.domain.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ContentService calendarService;
    private final CollectibleService collectibleService;

    // 메인페이지
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // 내실
    @GetMapping("/collectible")
    public String getCharacterCollectible(Model model, HttpSession session) {
        Member member = (Member) session.getAttribute("member"); // http 세션에서 가져온 닉네임
        if(member == null) {
            return "member/signin";
        }
        List<CollectiblePointSummaryDTO> collectibleItemList = collectibleService.getCollectiblePointSummary(member.getMemberId());
        model.addAttribute("collectibleItemList", collectibleItemList);
        return "collectible/collectible"; // 결과 뷰로 이동
    }

    // 테스트 페이지
    @GetMapping("/test")
    public String character(Model model) {
        model.addAttribute("characterList", new ArrayList<CharacterInfo>());
        return "test/index";
    }

    @GetMapping("/test/characters")
    public Mono<String> getCharacterInfo(@RequestParam String characterName, Model model,
                                         HttpServletRequest request) {
        // 클라이언트가 입력한 캐릭터 닉네임을 http 세션에 저장
        HttpSession session = request.getSession();
        session.setAttribute("nickname", characterName);
        Mono<List<CharacterInfo>> characterInfoMono = collectibleService.getCharacterInfo(characterName);
        return characterInfoMono.flatMap(characterInfoList -> {
            model.addAttribute("characterList", characterInfoList);
            return Mono.just("test/characters");
        });
    }
}