package com.teamProject.lostArkProject.alarm.dao;

import com.teamProject.lostArkProject.alarm.domain.Alarm;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AlarmDAO {
    // 데이터 저장
    void insertAlarm(@Param("memberId") String memberId,
                     @Param("contentName") String contentName);

    // 데이터 조회
    List<Alarm> getAllAlarm(String memberId);
    
    // 데이터 삭제
    void deleteByMemberIdAndContentName(@Param("memberId") String memberId,
                                        @Param("contentName") String contentName);

    // 데이터 존재 여부 확인
    boolean existsByMemberIdAndContentName(@Param("memberId") String memberId,
                                           @Param("contentName") String contentName);

}
