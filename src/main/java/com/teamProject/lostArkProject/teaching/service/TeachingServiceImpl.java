package com.teamProject.lostArkProject.teaching.service;

import com.teamProject.lostArkProject.teaching.dao.TeachingDAO;
import com.teamProject.lostArkProject.teaching.dto.MenteeDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorDTO;
import com.teamProject.lostArkProject.teaching.dto.MentorListDTO;
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
        if (mentorDTO.getMentorContentId() != null && !mentorDTO.getMentorContentId().isEmpty()) {
            // 쉼표로 구분된 문자열을 배열로 변환
            String[] contentIds = mentorDTO.getMentorContentId().split(", ");
            for (String contentId : contentIds) {
                teachingDAO.insertMentorContent(mentorDTO.getMentorMemberId(), contentId.trim());
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
   public List<MentorListDTO> getMentorList() {
       List<Map<String, Object>> mentorList = Optional.ofNullable(teachingDAO.getMentorList()).orElse(Collections.emptyList());
       List<Map<String, Object>> mentorContentList = Optional.ofNullable(teachingDAO.getMentorContent()).orElse(Collections.emptyList());
       List<Map<String, Object>> memberCharacterList = Optional.ofNullable(teachingDAO.getMemberCharacter()).orElse(Collections.emptyList());

       List<MentorListDTO> resultList = new ArrayList<>();

       for (Map<String, Object> mentor : mentorList) {
           MentorListDTO dto = new MentorListDTO();
           dto.setMentorMemberId(String.valueOf(mentor.get("mentorMemberId")));
           dto.setMentorWantToSay(String.valueOf(mentor.get("mentorWantToSay")));

           // mentorContentIds 처리
           String mentorMemberId = String.valueOf(mentor.get("mentorMemberId"));
           mentorContentList.stream()
                   .filter(content -> String.valueOf(content.get("mentorMemberId")).equals(mentorMemberId))
                   .findFirst()
                   .ifPresent(content -> {
                       String contentIdsString = String.valueOf(content.get("mentorContentNames"));
                       List<String> contentIds = Arrays.asList(contentIdsString.split(","));
                       dto.setMentorContentIds(contentIds);
                   });

           // memberCharacterList 처리
           memberCharacterList.stream()
                   .filter(character -> String.valueOf(character.get("mentorMemberId")).equals(mentorMemberId))
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
    public List<MentorListDTO> getMentorDetail(String mentorMemberId) {
        Map<String, Map<String, Object>> mentorMap = teachingDAO.getMentorList().stream()
                .collect(Collectors.toMap(m -> String.valueOf(m.get("mentorMemberId")), m -> m));
        Map<String, Map<String, Object>> contentMap = teachingDAO.getMentorContent().stream()
                .collect(Collectors.toMap(c -> String.valueOf(c.get("mentorMemberId")), c -> c));
        Map<String, Map<String, Object>> characterMap = teachingDAO.getMemberCharacter().stream()
                .collect(Collectors.toMap(c -> String.valueOf(c.get("mentorMemberId")), c -> c));

        List<MentorListDTO> resultList = new ArrayList<>();
        Map<String, Object> mentor = mentorMap.get(mentorMemberId);
        if (mentor != null) {
            MentorListDTO dto = new MentorListDTO();
            dto.setMentorMemberId(mentorMemberId);
            dto.setMentorWantToSay(String.valueOf(mentor.get("mentorWantToSay")));

            Map<String, Object> content = contentMap.get(mentorMemberId);
            if (content != null) {
                String contentIdsString = String.valueOf(content.get("mentorContentNames"));
                dto.setMentorContentIds(Arrays.asList(contentIdsString.split(",")));
            }

            Map<String, Object> character = characterMap.get(mentorMemberId);
            if (character != null) {
                dto.setCharacterNickname(String.valueOf(character.get("characterNickname")));
                dto.setItemLevel(String.valueOf(character.get("itemLevel")));
                dto.setServerName(String.valueOf(character.get("serverName")));
            }

            resultList.add(dto);
        }

        return resultList;
    }






}
