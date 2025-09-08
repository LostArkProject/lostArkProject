package com.teamProject.lostArkProject.collectible.controller;

import com.teamProject.lostArkProject.collectible.domain.CharacterInfo;
import com.teamProject.lostArkProject.collectible.domain.RecommendCollectible;
import com.teamProject.lostArkProject.collectible.dto.CollectiblePointSummaryDTO;
import com.teamProject.lostArkProject.collectible.dto.RecommendCollectibleFullDTO;
import com.teamProject.lostArkProject.collectible.service.CollectibleService;
import com.teamProject.lostArkProject.member.domain.Member;
import com.teamProject.lostArkProject.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "내실 RestAPI", description = "CollectibleRestController")
public class CollectibleRestController {

    private final CollectibleService collectibleService;
    private final MemberService memberService;

    public CollectibleRestController(CollectibleService collectibleService, MemberService memberService) {
        this.collectibleService = collectibleService;
        this.memberService = memberService;
    }

    //필요한가?
    @GetMapping("/characters/{characterName}/siblings")
    @Operation(summary = "캐릭터 정보", description = "캐릭터 정보를 조회합니다.")
    public Mono<List<CharacterInfo>> getCharacterInfo(@PathVariable String characterName) {
        return memberService.getCharacterInfo(characterName);
    }

    // collectibles url로 GET 요청 시 json 데이터 반환
    @GetMapping("/collectibles")
    @Operation(summary = "계정 요약 내실 정보", description = "계정의 요약 내실 정보를 조회합니다.")
    public List<CollectiblePointSummaryDTO> getCharacterCollectable(HttpSession session) {
        Member member = (Member) session.getAttribute("member"); // http 세션에서 가져온 닉네임
        return collectibleService.getCollectiblePointSummary(member.getMemberId());
    }

    @PatchMapping("/collectible/clear-status")
    @Operation(summary = "내실 성공 상태 수정", description = "내실의 성공 상태를 수정합니다.")
    public ResponseEntity<Void> updateClearStatus(HttpServletRequest request, @RequestBody Map<String, String> requestMap) {
        HttpSession session = request.getSession();
        Member member = (Member) session.getAttribute("member");

        boolean ok = collectibleService.updateCleared(
                Integer.parseInt(requestMap.get("recommendCollectibleID")),
                Boolean.parseBoolean(requestMap.get("cleared")),
                member.getMemberId()
        );
        if (ok) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/collectible/getRecommendCollectibleList")
    @Operation(summary = "추천 내실 정보 일부 조회", description = "추천 내실 일부 정보를 조회합니다.")
    public List<RecommendCollectible> getRecommendCollectibleList(HttpSession session){
        Member member = (Member) session.getAttribute("member");
        return collectibleService.getRecommendCollectible(member.getMemberId());
    }

    @GetMapping("/collectible/getRecommendFullCollectibleList")
    @Operation(summary = "추천 내실 정보 전체 조회", description = "추천 내실 전체 정보를 조회합니다.")
    public List<RecommendCollectibleFullDTO> getRecommendFullCollectibleList(HttpSession session){
        Member member = (Member) session.getAttribute("member");
        return collectibleService.getRecommendFullCollectible(member.getMemberId());
    }

    @PatchMapping("/collectible/clear")
    @Operation(summary = "추천 내실 성공", description = "선택한 추천 내실을 달성 상태로 바꿉니다.")
    public void clearCollectible(HttpSession session, @RequestBody Map<String, String> requestMap) {
        Member member = (Member) session.getAttribute("member");
        int collectibleId = Integer.parseInt(requestMap.get("collectibleId"));
        collectibleService.clearCollectible(member.getMemberId(), collectibleId);
    }

    public static class ClearStatusRequest {
        private int recommendCollectibleID;
        private boolean cleared;
        // getters/setters
    }

}