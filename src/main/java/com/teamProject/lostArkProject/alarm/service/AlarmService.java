package com.teamProject.lostArkProject.alarm.service;

import com.teamProject.lostArkProject.alarm.dao.AlarmDAO;
import com.teamProject.lostArkProject.alarm.domain.Alarm;
import com.teamProject.lostArkProject.content.dao.ContentDAO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmService {
    private final AlarmDAO alarmDAO;
    private final ContentDAO contentDAO;

    // 특정 사용자의 알람 설정 데이터를 가져오는 메서드
    public List<Alarm> getAllAlarm(String memberId) {
        return alarmDAO.getAllAlarm(memberId);
    }

    // 특정 컨텐츠의 알람을 설정하는 메서드
    public boolean toggleAlarm(String memberId, String contentName) throws Exception {
        if (contentName.isEmpty()) {
            throw new Exception("잘못된 컨텐츠명이 입력되었습니다." + contentName);
        }

        boolean isAlarmEnabled;

        if (alarmDAO.existsByMemberIdAndContentName(memberId, contentName)) {
            alarmDAO.deleteByMemberIdAndContentName(memberId, contentName);
            isAlarmEnabled = false;
        } else {
            alarmDAO.insertAlarm(memberId, contentName);
            isAlarmEnabled = true;
        }
        log.info("현재 알람 상태: {}", isAlarmEnabled);

        return isAlarmEnabled;
    }
}
