package com.teamProject.lostArkProject.alarm.service;

import com.teamProject.lostArkProject.alarm.dao.AlarmDAO;
import com.teamProject.lostArkProject.alarm.domain.Alarm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    private final AlarmDAO alarmDAO;

    // 특정 사용자의 알림 설정 데이터를 가져오는 메서드
    public List<Alarm> getAllAlarm(String memberId) {
        return alarmDAO.getAllAlarm(memberId);
    }

    // 특정 사용자의 알림을 설정하는 메서드
    public void insertAlarm(Alarm alarm) {
        if (alarm.getContentNumber() == 0) {
            throw new IllegalStateException("contentNumber 값이 유효하지 않습니다.");
        }
        if (alarmDAO.insertAlarm(alarm) != 1) {
            throw new RuntimeException("알림 설정 작업이 정상적으로 완료되지 않았습니다.");
        }
    }

    // 특정 알림을 해제하는 메서드
    public void deleteAlarm(String memberId, String contentName) {
        if (contentName.trim().isEmpty()) {
            throw new IllegalStateException("컨텐츠명이 유효하지 않습니다.");
        }
        if (alarmDAO.deleteByMemberAndContent(memberId, contentName) != 1) {
            throw new RuntimeException("삭제하려는 데이터가 존재하지 않습니다.");
        }
    }
}
