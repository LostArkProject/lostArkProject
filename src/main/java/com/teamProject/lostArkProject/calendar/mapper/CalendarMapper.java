package com.teamProject.lostArkProject.calendar.mapper;

import com.teamProject.lostArkProject.calendar.domain.Calendar;
import com.teamProject.lostArkProject.calendar.domain.Item;
import com.teamProject.lostArkProject.calendar.domain.RewardItem;
import com.teamProject.lostArkProject.calendar.domain.StartTime;

import java.util.List;

public interface CalendarMapper {
    int insertCalendar(Calendar calendar);
    int insertStartTime(List<StartTime> startTimes);
    int insertRewardItem(List<RewardItem> rewardItems);
    int insertItem(List<Item> items);
    List<Calendar> selectCalendar();
    List<Calendar> selectAllCalendarTable();
}
