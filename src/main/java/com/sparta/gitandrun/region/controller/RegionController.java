package com.sparta.gitandrun.region.controller;

import com.sparta.gitandrun.common.entity.ApiResDto;
import com.sparta.gitandrun.region.dto.RegionRequestDto;
import com.sparta.gitandrun.region.entity.Region;
import com.sparta.gitandrun.region.service.RegionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    // JSON 형식으로 데이터를 받아 지역 생성
    @PostMapping
    public ResponseEntity<?> createRegion(@RequestBody RegionRequestDto regionRequest) {
        try {
            Region region = regionService.createRegion(regionRequest.getRegionId(), regionRequest.getRegionName(), regionRequest.getHirnkRegionId());
            return ResponseEntity.ok(new ApiResDto("지역이 성공적으로 생성되었습니다.", 200));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResDto("지역 생성에 실패했습니다.", 500));
        }
    }

    // 전체 지역 조회
    @GetMapping
    public ResponseEntity<?> getAllRegions() {
        try {
            List<Region> regions = regionService.getAllRegions();
            return ResponseEntity.ok(regions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResDto("지역 목록을 불러오는데 실패했습니다.", 500));
        }
    }

    // ID로 지역 조회
    @GetMapping("/{regionId}")
    public ResponseEntity<?> getRegionById(@PathVariable Long regionId) {
        return regionService.getRegionById(regionId)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResDto("해당 ID를 가진 지역을 찾을 수 없습니다: " + regionId, 404)));
    }

    // JSON 형식으로 데이터를 받아 지역 수정
    @PutMapping("/{regionId}")
    public ResponseEntity<ApiResDto> updateRegion(@PathVariable Long regionId,
                                                  @RequestBody RegionRequestDto regionRequest) {
        try {
            regionService.updateRegion(regionId, regionRequest.getRegionName(), regionRequest.getHirnkRegionId());
            return ResponseEntity.ok(new ApiResDto("지역이 성공적으로 수정되었습니다.", 200));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResDto(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResDto("지역 수정에 실패했습니다.", 500));
        }
    }

    // ID로 지역 삭제
    @DeleteMapping("/{regionId}")
    public ResponseEntity<ApiResDto> deleteRegion(@PathVariable Long regionId) {
        try {
            regionService.deleteRegion(regionId);
            return ResponseEntity.ok(new ApiResDto("지역이 성공적으로 삭제되었습니다.", 200));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResDto(e.getMessage(), 404));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResDto("지역 삭제에 실패했습니다.", 500));
        }
    }
}
