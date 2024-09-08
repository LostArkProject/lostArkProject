package com.teamProject.lostArkProject.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
class CollectiblePoint {
    @JsonProperty("PointName")
    private String pointName;
    @JsonProperty("Point")
    private int point;
    @JsonProperty("MaxPoint")
    private int maxPoint;
}

@Getter
@Setter
public class CollectibleItem {
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Icon")
    private String icon;
    @JsonProperty("Point")
    private int point;
    @JsonProperty("MaxPoint")
    private int maxPoint;
    @JsonProperty("CollectiblePoints")
    private List<CollectiblePoint> collectiblePoints;
}
