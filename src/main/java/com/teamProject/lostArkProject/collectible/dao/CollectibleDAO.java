package com.teamProject.lostArkProject.collectible.dao;

import com.teamProject.lostArkProject.collectible.domain.CollectiblePoint;
import com.teamProject.lostArkProject.collectible.domain.RecommendCollectible;
import com.teamProject.lostArkProject.collectible.dto.CollectiblePointSummaryDTO;
import com.teamProject.lostArkProject.collectible.dto.RecommendCollectibleFullDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectibleDAO {
    //데이터 저장
    void insertCollectiblePoint(CollectiblePoint collectiblePoint);
    void insertClearCollectible(String memberId, int clearCollectibleId);
    void insertRecommendCollectible(RecommendCollectible recommendCollectible);

    //데이터 조회
    List<RecommendCollectible> getRecommendCollectible(String memberId);
    List<RecommendCollectibleFullDTO> getRecommendFullCollectible(String memberId);
    List<CollectiblePointSummaryDTO> getCollectiblePointSummary(String memberId);

    //데이터 삭제
    void deleteClearCollectible(String memberId, int recommendCollectibleID);
    void deleteCollectible(String memberId);
    void deleteRecommendCollectible();
}
