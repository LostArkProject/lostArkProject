package com.teamProject.lostArkProject.content.controller;

import com.teamProject.lostArkProject.content.dto.ContentDTO;
import com.teamProject.lostArkProject.content.dto.ContentStartTimeDTO;
import com.teamProject.lostArkProject.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentRestController {
    private final ContentService contentService;

    // content 데이터 조회
    @GetMapping("/contents")
    public ResponseEntity<List<ContentDTO>> getContentsAll() {
        return ResponseEntity.ok(contentService.getContentsAll());
    }

    // content, start_time 데이터 조회
    @GetMapping("/contents/start-time")
    public ResponseEntity<List<ContentStartTimeDTO>> getContentStartTimes() {
        return ResponseEntity.ok(contentService.getContentStartTimes());
    }
}
