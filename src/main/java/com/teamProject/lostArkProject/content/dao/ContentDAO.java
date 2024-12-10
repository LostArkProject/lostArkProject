package com.teamProject.lostArkProject.content.dao;

import com.teamProject.lostArkProject.content.domain.Content;
import com.teamProject.lostArkProject.content.domain.Reward;
import com.teamProject.lostArkProject.content.domain.StartTime;

import java.util.List;

public interface ContentDAO {
    // 데이터 저장
    void saveContent(Content content);
    void saveStartTime(List<StartTime> startTimes);
    void saveReward(List<Reward> rewards);

    // 데이터 조회
    List<Content> getContents();

    // 데이터 삭제
    void deleteAll();
    void deleteContent();
    void deleteStartTime();
    void deleteReward();
}
