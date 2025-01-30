package com.teamProject.lostArkProject.teaching.service;

import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorListDTO;

import java.util.List;


public interface TeachingService {
    public void newMentor(MentorDTO mentorDTO);
    public void newMentee(MenteeDTO menteeDTO);
    public List<MentorListDTO> getMentorList();
    public List<MentorListDTO> getMentorDetail(String mentorMemberId);

}
