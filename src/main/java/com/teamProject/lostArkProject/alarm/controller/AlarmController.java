package com.teamProject.lostArkProject.alarm.controller;

import com.teamProject.lostArkProject.alarm.domain.Alarm;
import com.teamProject.lostArkProject.alarm.service.AlarmService;
import com.teamProject.lostArkProject.member.domain.Member;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AlarmController {
    private final AlarmService alarmService;

    // 특정 사용자의 알람 설정 데이터를 가져오는 메서드
    @GetMapping("/api/alarm/member/{memberId}")
    public ResponseEntity<List<Alarm>> getAllAlarm(@PathVariable("memberId") String memberId,
                                                   HttpSession session) {
        Member member = (Member) session.getAttribute("member");

        // 로그인하지 않은 경우
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<Alarm> alarms = alarmService.getAllAlarm(memberId);
        log.info("현재 로그인한 유저의 알람 설정 종류: {}", alarms);
        return ResponseEntity.ok(alarms);
    }

    // 특정 컨텐츠의 알람을 설정하는 메서드
    @PostMapping("/api/alarm/member/{memberId}")
    public ResponseEntity<?> toggleAlarm(@PathVariable("memberId") String memberId,
                                         @RequestBody String contentName) {
        boolean result = alarmService.toggleAlarm(memberId, contentName);
        return ResponseEntity.ok(result);
    }

    // 특정 컨텐츠의 알림 설정을 해제하는 메서드
    @DeleteMapping("/api/alarm/member/{memberId}")
    public ResponseEntity<?> deleteAlarm(@PathVariable("memberId") String memberId,
                                         @RequestBody String contentName) {
        boolean result = alarmService.deleteAlarm(memberId, contentName);

        if (!result) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok("삭제 성공");
    }
}
