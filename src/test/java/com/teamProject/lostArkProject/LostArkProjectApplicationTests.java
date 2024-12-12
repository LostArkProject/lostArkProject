package com.teamProject.lostArkProject;

import com.teamProject.lostArkProject.content.dao.ContentDAO;
import com.teamProject.lostArkProject.content.domain.Reward;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@SpringBootTest
class LostArkProjectApplicationTests {

	@Autowired
	private ContentDAO contentDAO;

	@Test
	public void save() {
		List<Reward> rewards = List.of(
			new Reward(1, "Reward 1", 10, "icon1.png", "High"),
			new Reward(2, "Reward 2", 15, "icon2.png", "Medium")
		);

		saveRewards(rewards);
	}

	@Transactional
	public void saveRewards(List<Reward> rewards) {
		if (rewards == null || rewards.isEmpty()) {
			System.out.println("No rewards to save.");
			return;
		}

		contentDAO.saveReward(rewards);
	}

}
