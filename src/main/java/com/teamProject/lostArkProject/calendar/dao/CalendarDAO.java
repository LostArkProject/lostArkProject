package com.teamProject.lostArkProject.calendar.dao;

import com.teamProject.lostArkProject.calendar.domain.Content;
import com.teamProject.lostArkProject.calendar.domain.Reward;
import com.teamProject.lostArkProject.calendar.domain.StartTime;

import java.util.List;

public interface CalendarDAO {
    void saveContent(Content content);
    void saveStartTime(List<StartTime> startTimes);
    void saveReward(List<Reward> rewards);
    void deleteAll();
    void deleteContent();
    void deleteStartTime();
    void deleteReward();
}
