package com.teamProject.lostArkProject.alarm.dao;

import com.teamProject.lostArkProject.alarm.domain.Alarm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmDAO {
    // 데이터 저장
    int insertAlarm(Alarm alarm);

    // 데이터 조회
    List<Alarm> getAllAlarm(String memberId);
    
    // 데이터 삭제
    int deleteByMemberAndContent(@Param("memberId") String memberId,
                                 @Param("contentName") String contentName);

    // 데이터 존재 여부 확인
    boolean existsByMemberAndContent(@Param("memberId") String memberId,
                                     @Param("contentName") String contentName);

}
