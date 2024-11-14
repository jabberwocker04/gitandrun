package com.sparta.gitandrun.store.controller;

import com.sparta.gitandrun.common.entity.ApiResDto;
import com.sparta.gitandrun.store.dto.StoreRequestDto;
import com.sparta.gitandrun.store.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // 가게 등록
    @PostMapping
    public ResponseEntity<ApiResDto> createStore(@RequestParam Long userId, @RequestBody StoreRequestDto storeRequestDto) {
        try {
            storeService.createStore(userId, storeRequestDto);
            ApiResDto response = new ApiResDto("가게가 성공적으로 등록되었습니다.", 201, null);
            return ResponseEntity.status(201).body(response);
        } catch (IllegalArgumentException e) {
            ApiResDto errorResponse = new ApiResDto("가게 생성 권한이 없습니다.", 403);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        }
    }



    // ID로 조회
    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreDetails(
            @PathVariable UUID storeId,
            @RequestParam Long userId) {
        try {
            return storeService.getStoreDetails(storeId, userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResDto("가게 또는 사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()));
        }
    }

    // 전체 가게 조회
    @GetMapping
    public ResponseEntity<?> getAllStores(@RequestParam Long userId) {
        try {
            return ResponseEntity.ok(storeService.getAllStores(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResDto("사용자를 찾을 수 없습니다.", HttpStatus.NOT_FOUND.value()));
        }
    }

    // 가게 수정
    @PatchMapping("/{storeId}")
    public ResponseEntity<ApiResDto> updateStore(
            @PathVariable UUID storeId,
            @RequestParam Long userId,
            @RequestBody StoreRequestDto updatedStoreDto) {
        try {
            storeService.updateStore(storeId, userId, updatedStoreDto);
            return ResponseEntity.ok(new ApiResDto("가게 정보가 성공적으로 수정되었습니다.", HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResDto("수정 권한이 없습니다.", HttpStatus.FORBIDDEN.value()));
        }
    }

    // 가게 삭제
    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResDto> deleteStore(
            @PathVariable UUID storeId,
            @RequestParam Long userId) {
        try {
            storeService.deleteStore(storeId, userId);
            return ResponseEntity.ok(new ApiResDto("가게가 성공적으로 삭제되었습니다.", HttpStatus.OK.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResDto("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN.value()));
        }
    }

    // 카테고리로 검색
    @GetMapping("/search")
    public ResponseEntity<?> searchStores(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(storeService.searchStores(categoryId, sortField, page, size, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResDto("검색 권한이 없습니다.", HttpStatus.FORBIDDEN.value()));
        }
    }

    // 키워드로 검색
    @GetMapping("/search/keyword")
    public ResponseEntity<?> searchStoresByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam Long userId) {
        try {
            return ResponseEntity.ok(storeService.searchStoresByKeyword(keyword, sortField, page, size, userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResDto("검색 권한이 없습니다.", HttpStatus.FORBIDDEN.value()));
        }
    }
}
