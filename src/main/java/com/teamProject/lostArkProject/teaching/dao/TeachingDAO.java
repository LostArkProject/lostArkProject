package com.teamProject.lostArkProject.teaching.dao;

import com.teamProject.lostArkProject.teaching.dto.MenteeApplyDTO;
import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import org.apache.ibatis.annotations.Mapper;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


@Mapper
public interface TeachingDAO {
    public void newMentor(MentorDTO mentorDTO);
    void insertMentorContent(@Param("mentorMemberId") String mentorMemberId, @Param("mentorContentId") String contentId);
    public void newMentee(MenteeDTO menteeDTO);
    public List<Map<String,Object>> getMentorList();
    public List<Map<String,Object>> getMentorContent();
    public List<Map<String,Object>> getMemberCharacter();
//    public Map<String,Object> getMentorListDetail(long mentorId);
}
