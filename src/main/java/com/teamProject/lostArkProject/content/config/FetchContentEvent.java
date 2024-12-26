package com.teamProject.lostArkProject.content.config;

import com.teamProject.lostArkProject.content.service.ContentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FetchContentEvent implements ApplicationListener<ApplicationReadyEvent> {

    private final ContentService contentService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("데이터베이스 초기화 작업을 시작합니다.");
        contentService.saveContent();
    }
}
