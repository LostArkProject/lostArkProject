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
    // 새로운 MENTOR_CONTENT 테이블에 삽입 (다중 콘텐츠 데이터 처리)
    void insertMentorContent(@Param("mentorMemberId") String mentorMemberId, @Param("mentorContentId") Integer contentId);
    public void newMentee(MenteeDTO menteeDTO);
    //public void newMenteeApply(MenteeApplyDTO menteeApplyDTO);
    public List<Map<String,Object>> getMentorList();
    //public Map<String,Object> getMentorListDetail(long mentorId);
}
