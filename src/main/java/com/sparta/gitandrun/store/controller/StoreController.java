package com.sparta.gitandrun.store.controller;

import com.sparta.gitandrun.common.entity.ApiResDto;
import com.sparta.gitandrun.store.dto.StoreRequestDto;
import com.sparta.gitandrun.store.entity.Store;
import com.sparta.gitandrun.store.service.StoreService;
import com.sparta.gitandrun.user.entity.Role;
import com.sparta.gitandrun.user.jwt.JwtUtil;
import com.sparta.gitandrun.user.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final JwtUtil jwtUtil;

    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @PostMapping("/admin")
    public ResponseEntity<ApiResDto> createStore(
            @RequestBody StoreRequestDto storeRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            // 현재 로그인한 사용자의 ID 가져오기
            Long userId = userDetails.getUser().getUserId();

            // 가게 등록 요청
            Store createdStore = storeService.createStore(userId, storeRequestDto);

            // 성공 응답
            ApiResDto response = new ApiResDto("가게가 성공적으로 등록되었습니다.", 201, createdStore);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // 예외 처리
            ApiResDto errorResponse = new ApiResDto("요청 처리 중 오류가 발생했습니다.", 500);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // ADMIN 전체 가게 조회
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @GetMapping("/admin")
    public ResponseEntity<ApiResDto> getAllStores() {
        try {
            // 전체 가게 조회
            var stores = storeService.getAllStoresForAdmin();
            // 성공 응답 반환
            ApiResDto response = new ApiResDto("전체 가게 조회 성공", 200, stores);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 발생 시 오류 응답
            ApiResDto errorResponse = new ApiResDto("전체 가게 조회 중 오류가 발생했습니다.", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // ~ Admin 전체 가게 조회
    @GetMapping()
    public ResponseEntity<ApiResDto> getAllStoresForUser() {
        try {
            // 일반 사용자를 위한 전체 가게 조회
            var stores = storeService.getAllStoresForUser();
            // 성공 응답 반환
            ApiResDto response = new ApiResDto("전체 가게 조회 성공", 200, stores);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 예외 발생 시 오류 응답
            ApiResDto errorResponse = new ApiResDto("전체 가게 조회 중 오류가 발생했습니다.", 500);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // 가게 상세 정보 조회 (권한에 따라 다른 정보 제공)
    @GetMapping("/{storeId}")
    public ResponseEntity<?> getStoreDetails(@PathVariable UUID storeId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long userId = userDetails.getUser().getUserId(); // 로그인한 유저의 userId

        return storeService.getStoreDetails(storeId, userId);  // 권한에 맞는 가게 정보 반환
    }

    // 관리자용 Soft-Delete된 가게 조회
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/deleted")
    public ResponseEntity<ApiResDto> getSoftDeletedStores() {
        try {
            var stores = storeService.getSoftDeletedStores();
            ApiResDto response = new ApiResDto("Soft-Delete된 가게 조회 성공", HttpStatus.OK.value(), stores);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResDto errorResponse = new ApiResDto("Soft-Delete된 가게 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 관리자용 Soft-Delete되지 않은 가게 조회
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/not-deleted")
    public ResponseEntity<ApiResDto> getNonDeletedStores() {
        try {
            var stores = storeService.getNonDeletedStores();
            ApiResDto response = new ApiResDto("Soft-Delete되지 않은 가게 조회 성공", HttpStatus.OK.value(), stores);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResDto errorResponse = new ApiResDto("Soft-Delete되지 않은 가게 조회 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 관리자용 가게 수정
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @PatchMapping("/admin/{storeId}")
    public ResponseEntity<ApiResDto> updateStoreByAdmin(
            @PathVariable UUID storeId,
            @RequestBody StoreRequestDto storeRequestDto) {
        try {
            storeService.updateStoreByAdmin(storeId, storeRequestDto);
            ApiResDto response = new ApiResDto("가게가 성공적으로 수정되었습니다.", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResDto errorResponse = new ApiResDto("가게 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 일반 사용자용 가게 수정
    @Secured("ROLE_OWNER")
    @PatchMapping("/{storeId}")
    public ResponseEntity<ApiResDto> updateStoreByUser(
            @PathVariable UUID storeId,
            @RequestBody StoreRequestDto storeRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getUser().getUserId(); // 현재 로그인한 사용자의 ID
            storeService.updateStoreByUser(storeId, storeRequestDto, userId);
            ApiResDto response = new ApiResDto("가게가 성공적으로 수정되었습니다.", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            ApiResDto errorResponse = new ApiResDto("가게 수정 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ApiResDto errorResponse = new ApiResDto("서버 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // 관리자용 가게 소프트 딜리트
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @DeleteMapping("/admin/{storeId}")
    public ResponseEntity<ApiResDto> softDeleteStoreByAdmin(@PathVariable UUID storeId) {
        try {
            storeService.softDeleteStoreByAdmin(storeId);
            ApiResDto response = new ApiResDto("가게가 성공적으로 삭제되었습니다.", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResDto errorResponse = new ApiResDto("가게 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // 사용자용 가게 소프트 딜리트
    @DeleteMapping("/{storeId}")
    @Secured({"ROLE_OWNER", "ROLE_MANAGER", "ROLE_ADMIN"})
    public ResponseEntity<ApiResDto> softDeleteStoreByUser(
            @PathVariable UUID storeId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            Long userId = userDetails.getUser().getUserId(); // 인증된 사용자 ID 가져오기
            storeService.softDeleteStoreByOwner(storeId, userId);
            ApiResDto response = new ApiResDto("가게가 성공적으로 삭제되었습니다.", HttpStatus.OK.value());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            ApiResDto errorResponse = new ApiResDto("가게 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // 카테고리로 검색 (관리자용)
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @GetMapping("/admin/search/category")
    public ResponseEntity<ApiResDto> searchStoresByCategoryAsAdmin(
            @RequestParam UUID categoryId,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            ApiResDto response = new ApiResDto("검색 성공", 200,
                    storeService.searchStoresByCategory(categoryId, sortField, page, size, true));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResDto("검색 중 오류 발생", 500));
        }
    }

    // 카테고리로 검색 (사용자용)
    @GetMapping("/search/category")
    public ResponseEntity<ApiResDto> searchStoresByCategoryAsUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam UUID categoryId,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            ApiResDto response = new ApiResDto("검색 성공", 200,
                    storeService.searchStoresByCategory(categoryId, sortField, page, size, false));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResDto("검색 중 오류 발생", 500));
        }
    }

    // 키워드로 검색 (관리자용)
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    @GetMapping("/admin/search/keyword")
    public ResponseEntity<ApiResDto> searchStoresByKeywordAsAdmin(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            Page<?> searchResults = storeService.searchStoresByKeyword(keyword, sortField, page, size, true);
            ApiResDto response = new ApiResDto("검색 성공", 200, searchResults);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResDto("검색 중 오류 발생", 500));
        }
    }

    // 키워드로 검색 (일반 사용자용)
    @GetMapping("/search/keyword")
    public ResponseEntity<ApiResDto> searchStoresByKeyword(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            boolean isAdmin = userDetails.getUser().getRole().equals(Role.ADMIN);
            Page<?> searchResults = storeService.searchStoresByKeyword(keyword, sortField, page, size, isAdmin);
            ApiResDto response = new ApiResDto("검색 성공", 200, searchResults);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResDto("검색 중 오류 발생", 500));
        }
    }


    // 지역 이름으로 가게 조회
    @GetMapping("/search/region")
    public ResponseEntity<ApiResDto> getStoresByRegionName(
            @RequestParam Long userId,
            @RequestParam String regionName) {
        try {
            List<?> stores = storeService.getStoresByRegionName(userId, regionName);
            return ResponseEntity.ok(new ApiResDto("가게 목록 조회 성공", 200, stores));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404)
                    .body(new ApiResDto("가게 목록 조회 실패: " + e.getMessage(), 404));
        }
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResDto> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResDto(ex.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    // Soft-Delete된 가게만 카테고리로 검색
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/search/category/deleted")
    public ResponseEntity<ApiResDto> searchDeletedStoresByCategory(
            @RequestParam UUID categoryId,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<?> stores = storeService.searchDeletedStoresByCategory(categoryId, sortField, page, size);
            return ResponseEntity.ok(new ApiResDto("Soft-Delete된 가게 카테고리 검색 성공", 200, stores));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResDto("Soft-Delete된 가게 카테고리 검색 중 오류 발생", 500));
        }
    }

    // Soft-Delete되지 않은 가게만 카테고리로 검색
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/search/category/not-deleted")
    public ResponseEntity<ApiResDto> searchNonDeletedStoresByCategory(
            @RequestParam UUID categoryId,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<?> stores = storeService.searchNonDeletedStoresByCategory(categoryId, sortField, page, size);
            return ResponseEntity.ok(new ApiResDto("Soft-Delete되지 않은 가게 카테고리 검색 성공", 200, stores));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResDto("Soft-Delete되지 않은 가게 카테고리 검색 중 오류 발생", 500));
        }
    }

    // Soft-Delete된 가게만 키워드로 검색
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/search/keyword/deleted")
    public ResponseEntity<ApiResDto> searchDeletedStoresByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<?> stores = storeService.searchDeletedStoresByKeyword(keyword, sortField, page, size);
            return ResponseEntity.ok(new ApiResDto("Soft-Delete된 가게 키워드 검색 성공", 200, stores));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResDto("Soft-Delete된 가게 키워드 검색 중 오류 발생", 500));
        }
    }

    // Soft-Delete되지 않은 가게만 키워드로 검색
    @Secured("ROLE_ADMIN")
    @GetMapping("/admin/search/keyword/not-deleted")
    public ResponseEntity<ApiResDto> searchNonDeletedStoresByKeyword(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<?> stores = storeService.searchNonDeletedStoresByKeyword(keyword, sortField, page, size);
            return ResponseEntity.ok(new ApiResDto("Soft-Delete되지 않은 가게 키워드 검색 성공", 200, stores));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResDto("Soft-Delete되지 않은 가게 키워드 검색 중 오류 발생", 500));
        }
    }
}

