package com.teamProject.lostArkProject.collectible.dao;

import com.teamProject.lostArkProject.collectible.domain.CollectiblePoint;
import com.teamProject.lostArkProject.collectible.dto.CollectiblePointSummaryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectibleDAO {
    void insertCollectiblePoint(CollectiblePoint collectiblePoint);
    List<CollectiblePointSummaryDTO> getCollectiblePointSummary(String memberId);
}
