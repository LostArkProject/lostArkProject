package com.teamProject.lostArkProject.alarm.controller;

import com.teamProject.lostArkProject.alarm.domain.Alarm;
import com.teamProject.lostArkProject.alarm.service.AlarmService;
import com.teamProject.lostArkProject.member.config.SessionUtils;
import com.teamProject.lostArkProject.member.domain.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AlarmController {
    private final AlarmService alarmService;

    // 특정 사용자의 알림 설정 데이터를 가져오는 메서드
    @GetMapping("/api/alarm")
    public ResponseEntity<List<Alarm>> getAllAlarm(HttpSession session) {
        String memberId = SessionUtils.getMemberId(session);

        List<Alarm> alarms = alarmService.getAllAlarm(memberId);
        log.info("알람 데이터: {}", alarms);
        return ResponseEntity.ok(alarms);
    }

    // 특정 사용자의 알림을 설정하는 메서드
    @PostMapping("/api/alarm")
    public ResponseEntity<?> insertAlarm(HttpSession session,
                                         @Validated @RequestBody Alarm alarm,
                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException("유효하지 않은 입력입니다.");
        }

        String memberId = SessionUtils.getMemberId(session);
        alarm.setMemberId(memberId);

        alarmService.insertAlarm(alarm);
        return ResponseEntity.ok("알림 설정에 성공했습니다.");
    }

    // 특정 알림을 해제하는 메서드
    @DeleteMapping("/api/alarm/{contentName}")
    public ResponseEntity<?> deleteAlarm(HttpSession session,
                                         @Validated @PathVariable("contentName") String contentName) {
        String memberId = SessionUtils.getMemberId(session);

        alarmService.deleteAlarm(memberId, contentName);
        return ResponseEntity.ok("알림 해제에 성공했습니다.");
    }
}
