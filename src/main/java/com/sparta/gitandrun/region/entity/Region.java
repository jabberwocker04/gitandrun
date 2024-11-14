package com.sparta.gitandrun.region.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "p_region")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // 프록시 속성 무시
public class Region {

    @Id
    @Column(name = "region_id", nullable = false)
    private Long regionId;

    @Column(name = "region_name", nullable = false, length = 100)
    private String regionName;

    @Column(name = "hirnk_region_id")
    private Long hirnkRegionId;

    public Region(Long regionId, String regionName, Long hirnkRegionId) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.hirnkRegionId = hirnkRegionId;
    }
}
