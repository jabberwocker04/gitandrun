package com.sparta.gitandrun.region.service;

import com.sparta.gitandrun.region.entity.Region;
import com.sparta.gitandrun.region.repository.RegionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    // 초기 데이터 설정
    @PostConstruct
    @Transactional
    public void initializeRegions() {
        Optional<Region> existingRegion = regionRepository.findById(1L);
        if (existingRegion.isEmpty()) {
            Region seoulRegion = new Region();
            seoulRegion.setRegionName("서울시");
            seoulRegion.setHirnkRegionId(null);  // 최상위 지역일 경우 null로 설정
            regionRepository.save(seoulRegion);
        }
    }

    // 지역 생성
    @Transactional
    public Region createRegion(Long regionId, String regionName, Long hirnkRegionId) {
        Region region = new Region(regionId, regionName, hirnkRegionId);
        return regionRepository.save(region);
    }


    // 지역 전체 조회
    // Readonly
    public List<Region> getAllRegions() {
        return regionRepository.findAll();
    }

    // 지역 ID로 조회
    public Optional<Region> getRegionById(Long regionId) {
        return regionRepository.findById(regionId);
    }

    // 지역 수정
    @Transactional
    public Region updateRegion(Long regionId, String newRegionName, Long newHirnkRegionId) {
        Region region = regionRepository.findById(regionId)
                .orElseThrow(() -> new IllegalArgumentException("지역을 찾을 수 없습니다 : " + regionId));
        region.setRegionName(newRegionName);
        region.setHirnkRegionId(newHirnkRegionId);
        return regionRepository.save(region);
    }

    // 지역 삭제
    @Transactional
    public void deleteRegion(Long regionId) {
        if (!regionRepository.existsById(regionId)) {
            throw new IllegalArgumentException("지역을 찾을 수 없습니다 : " + regionId);
        }
        regionRepository.deleteById(regionId);
    }
}
