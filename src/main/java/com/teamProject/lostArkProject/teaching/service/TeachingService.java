package com.teamProject.lostArkProject.teaching.service;

import com.teamProject.lostArkProject.teaching.dto.MenteeApplyDTO;
import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;

import java.util.List;
import java.util.Map;


public interface TeachingService {
    public void newMentor(MentorDTO mentorDTO);
    public void newMentee(MenteeDTO menteeDTO);
    public void newMenteeApply(MenteeApplyDTO menteeApplyDTO);
    public List<Map<String,Object>> getMentorList();
    public Map<String, Object> getMentorListDetail(long mentorId);

}
