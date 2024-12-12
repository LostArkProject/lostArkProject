package com.teamProject.lostArkProject.teaching.dao;

import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MentorDAO {
    public void newMentor(MentorDTO mentorDTO);


}
