package com.teamProject.lostArkProject.controller;

import com.teamProject.lostArkProject.domain.CharacterInfo;
import com.teamProject.lostArkProject.service.LostArkAPIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

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
}