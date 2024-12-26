package com.teamProject.lostArkProject;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.teamProject.lostArkProject.**.dao") // 모든 기능별 하위 패키지의 mapper를 스캔
@EnableScheduling
public class LostArkProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LostArkProjectApplication.class, args);
	}

}
