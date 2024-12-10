package com.teamProject.lostArkProject;

import com.teamProject.lostArkProject.calendar.dao.CalendarDAO;
import com.teamProject.lostArkProject.calendar.domain.Reward;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
class LostArkProjectApplicationTests {

	@Autowired
	private CalendarDAO calendarDAO;

	@Test
	public void save() {
		List<Reward> rewards = List.of(
			new Reward("Content A", "Reward 1", 10, "icon1.png", "High"),
			new Reward("Content B", "Reward 2", 15, "icon2.png", "Medium")
		);

		saveRewards(rewards);
	}

	@Transactional
	public void saveRewards(List<Reward> rewards) {
		if (rewards == null || rewards.isEmpty()) {
			System.out.println("No rewards to save.");
			return;
		}

		calendarDAO.saveReward(rewards);
	}

}
