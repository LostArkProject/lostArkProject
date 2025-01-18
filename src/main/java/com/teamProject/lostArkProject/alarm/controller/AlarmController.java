package com.teamProject.lostArkProject.alarm.controller;

import com.teamProject.lostArkProject.alarm.domain.Alarm;
import com.teamProject.lostArkProject.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AlarmController {
    private final AlarmService alarmService;

    // 특정 사용자의 알람 설정 데이터를 가져오는 메서드
    @GetMapping("/api/alarm/member/{memberId}")
    public ResponseEntity<List<Alarm>> getAllAlarm(@PathVariable("memberId") String memberId) {
        List<Alarm> alarms = alarmService.getAllAlarm(memberId);
        log.info("getAllAlarm: {}", alarms);
        return ResponseEntity.ok(alarms);
    }

    // 특정 컨텐츠의 알람을 설정하는 메서드
    @PostMapping("/api/alarm/member/{memberId}/{contentId}")
    public ResponseEntity<?> toggleAlarm(@PathVariable("memberId") String memberId,
                                         @PathVariable("contentId") int contentId) throws Exception {
        boolean result = alarmService.toggleAlarm(memberId, contentId);
        return ResponseEntity.ok(result);
    }
}
