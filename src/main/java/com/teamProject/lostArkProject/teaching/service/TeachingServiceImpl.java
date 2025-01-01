package com.teamProject.lostArkProject.teaching.service;

import com.teamProject.lostArkProject.teaching.dao.TeachingDAO;
import com.teamProject.lostArkProject.teaching.dto.MenteeApplyDTO;
import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeachingServiceImpl implements TeachingService {

    @Autowired
    private TeachingDAO teachingDAO;

    @Override
    public void newMentor(MentorDTO mentorDTO) {
        // 1. MENTOR 테이블에 멘토 기본 정보 저장
        teachingDAO.newMentor(mentorDTO);

        // 2. MENTOR_CONTENT 테이블에 콘텐츠 정보 저장
        if (mentorDTO.getMentorContentId() != null) {
            for (Integer mentorContentId : mentorDTO.getMentorContentId()) {
                teachingDAO.insertMentorContent(mentorDTO.getMentorMemberId(), mentorContentId);
            }
        }
    }

    @Override
    public void newMentee(MenteeDTO menteeDTO) {
        teachingDAO.newMentee(menteeDTO);
    }

    //@Override
    //public void newMenteeApply(MenteeApplyDTO menteeApplyDTO){

   // }

    @Override
    public List<Map<String, Object>> getMentorList() {
        List<Map<String, Object>> result = teachingDAO.getMentorList();
        System.out.println("Mentor List: " + result);
        return result;
    }


    @Override
    public Map<String, Object> getMentorListDetail(long mentorId) {
        return Map.of();
    }

    ;

}
