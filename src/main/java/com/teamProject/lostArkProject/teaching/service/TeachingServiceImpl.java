package com.teamProject.lostArkProject.teaching.service;

import com.teamProject.lostArkProject.teaching.dao.TeachingDAO;
import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorLIstDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
   public List<MentorLIstDTO> getMentorList() {
       List<Map<String, Object>> mentorList = Optional.ofNullable(teachingDAO.getMentorList()).orElse(Collections.emptyList());
       List<Map<String, Object>> mentorContentList = Optional.ofNullable(teachingDAO.getMentorContent()).orElse(Collections.emptyList());
       List<Map<String, Object>> memberCharacterList = Optional.ofNullable(teachingDAO.getMemberCharacter()).orElse(Collections.emptyList());

       List<MentorLIstDTO> resultList = new ArrayList<>();

       for (Map<String, Object> mentor : mentorList) {
           MentorLIstDTO dto = new MentorLIstDTO();
           dto.setMentorMemberId(String.valueOf(mentor.get("mentorMemberId")));
           dto.setMentorWantToSay(String.valueOf(mentor.get("mentorWantToSay")));

           // mentorContentIds를 리스트로 설정
           List<String> contentIds = mentorContentList.stream()
                   .filter(content -> String.valueOf(content.get("mentorMemberId")).equals(String.valueOf(mentor.get("mentorMemberId"))))
                   .map(content -> String.valueOf(content.get("mentorContentId"))) // int -> String 변환
                   .collect(Collectors.toList());

           dto.setMentorContentIds(contentIds); // 리스트 직접 설정

           // memberCharacterList 처리
           memberCharacterList.stream()
                   .filter(character -> String.valueOf(character.get("mentorMemberId")).equals(String.valueOf(mentor.get("mentorMemberId"))))
                   .findFirst()
                   .ifPresent(character -> {
                       dto.setCharacterNickname(String.valueOf(character.get("characterNickname")));
                       dto.setItemLevel(String.valueOf(character.get("itemLevel")));
                       dto.setServerName(String.valueOf(character.get("serverName")));
                   });

           resultList.add(dto);
       }

       return resultList;
   }




    @Override
    public Map<String, Object> getMentorListDetail(long mentorId) {
        return Map.of();
    }

    ;

}
