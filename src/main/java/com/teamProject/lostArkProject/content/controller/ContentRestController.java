package com.teamProject.lostArkProject.content.controller;

import com.teamProject.lostArkProject.content.dto.ContentDTO;
import com.teamProject.lostArkProject.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ContentRestController {
    private final ContentService contentService;

    // content 데이터 조회
    @GetMapping("/content/all")
    public ResponseEntity<List<ContentDTO>> getContentsAll() {
        return ResponseEntity.ok(contentService.getContentsAll());
    }

    // content, start_time 데이터 조회
    @GetMapping("/content/start-time")
    public ResponseEntity<List<ContentDTO>> getContentsAndStartTimes() {
        return ResponseEntity.ok(contentService.getContentsAndStartTimes());
    }
}
