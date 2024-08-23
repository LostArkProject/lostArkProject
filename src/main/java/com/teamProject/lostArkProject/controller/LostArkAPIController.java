package com.teamProject.lostArkProject.controller;

import com.teamProject.lostArkProject.service.LostArkAPIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class LostArkAPIController {

    private final LostArkAPIService lostArkAPIService;

    public LostArkAPIController(LostArkAPIService LostArkAPIService) {
        this.lostArkAPIService = LostArkAPIService;
    }

    @GetMapping("/character/acac")
    public Mono<String> getCharacterInfo() {
        return lostArkAPIService.getCharacterInfo();
    }
}