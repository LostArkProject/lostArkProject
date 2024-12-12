package com.teamProject.lostArkProject.teaching.dao;

import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import org.apache.ibatis.annotations.Mapper;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;


@Mapper
public interface TeachingDAO {
    public void newMentor(MentorDTO mentorDTO);
    public void newMentee(MenteeDTO menteeDTO);


}
