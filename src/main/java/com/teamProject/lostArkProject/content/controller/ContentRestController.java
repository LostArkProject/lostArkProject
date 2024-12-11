package com.teamProject.lostArkProject.content.controller;

import com.teamProject.lostArkProject.content.domain.Content;
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

    // 외부 api에서 데이터를 가져와서 db에 저장
    @GetMapping("/content/fetch")
    public ResponseEntity<Mono<String>> fetchAndSaveContent() {
        return ResponseEntity.ok(contentService.saveContent());
    }

    // content 데이터 조회
    @GetMapping("/content/all")
    public ResponseEntity<List<ContentDTO>> getContentsAll() {
        return ResponseEntity.ok(contentService.getContentsAll());
    }

    //// calendar 데이터에서 calendarDTO 객체에 새로운 데이터를 담아서 반환
    //@GetMapping("/calendar")
    //public Mono<List<CalendarWithServerTimeDTO>> getCalendarsWithServerTime() {
    //   return calendarService.getCalendarWithServerTime();
    //}
}
