package com.teamProject.lostArkProject.teaching.service;

import com.teamProject.lostArkProject.teaching.dao.TeachingDAO;
import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeachingServiceImpl implements TeachingService {

    @Autowired
    private TeachingDAO teachingDAO;

    @Override
    public void newMentor(MentorDTO mentorDTO) {
        teachingDAO.newMentor(mentorDTO);
    }

    @Override
    public void newMentee(MenteeDTO menteeDTO) {
        teachingDAO.newMentee(menteeDTO);
    }
}
