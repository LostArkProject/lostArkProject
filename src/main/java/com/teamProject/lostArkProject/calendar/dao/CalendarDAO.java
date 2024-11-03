package com.teamProject.lostArkProject.calendar.dao;

import com.teamProject.lostArkProject.calendar.domain.Calendar;
import com.teamProject.lostArkProject.calendar.domain.Item;
import com.teamProject.lostArkProject.calendar.domain.RewardItem;
import com.teamProject.lostArkProject.calendar.domain.StartTime;

import java.util.List;

public interface CalendarDAO {
    int insertCalendar(Calendar calendar);
    int insertStartTime(List<StartTime> startTimes);
    int insertRewardItem(List<RewardItem> rewardItems);
    int insertItem(List<Item> items);
    List<Calendar> selectCalendar();
    List<StartTime> selectStartTime();
    List<RewardItem> selectRewardItem();
    List<Item> selectItem();
    List<Calendar> selectCalendarWithStartTime();
    List<Calendar> selectAllCalendarTable();
}
