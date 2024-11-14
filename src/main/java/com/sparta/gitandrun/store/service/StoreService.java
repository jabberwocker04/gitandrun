package com.sparta.gitandrun.store.service;

import com.sparta.gitandrun.category.entity.Category;
import com.sparta.gitandrun.category.repository.CategoryRepository;
import com.sparta.gitandrun.region.entity.Region;
import com.sparta.gitandrun.region.repository.RegionRepository;
import com.sparta.gitandrun.store.dto.FullStoreResponse;
import com.sparta.gitandrun.store.dto.LimitedStoreResponse;
import com.sparta.gitandrun.store.dto.StoreRequestDto;
import com.sparta.gitandrun.store.entity.Store;
import com.sparta.gitandrun.store.repository.StoreRepository;
import com.sparta.gitandrun.user.entity.Role;
import com.sparta.gitandrun.user.entity.User;
import com.sparta.gitandrun.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;

@Service
public class StoreService {

    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final CategoryRepository categoryRepository;

    public StoreService(StoreRepository storeRepository, UserRepository userRepository, RegionRepository regionRepository, CategoryRepository categoryRepository) {
        this.storeRepository = storeRepository;
        this.userRepository = userRepository;
        this.regionRepository = regionRepository;
        this.categoryRepository = categoryRepository;
    }

    // 가게 생성
    @Transactional
    public Store createStore(Long userId, StoreRequestDto storeRequestDto) {
        // 사용자 정보 조회
        User user = getUser(userId);

        // 권한 체크 - ADMIN 또는 OWNER만 가능
        if (user.getRole() != Role.ADMIN && user.getRole() != Role.OWNER) {
            throw new IllegalArgumentException("가게 생성 권한이 없습니다.");  // 권한이 없으면 예외 발생
        }

        // 카테고리 이름으로 카테고리 조회
        Category category = categoryRepository.findByName(storeRequestDto.getCategoryName())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + storeRequestDto.getCategoryName()));

        // regionId로 Region 조회
        Region region = regionRepository.findById(storeRequestDto.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다."));

        Store newStore = new Store();
        populateStoreFields(newStore, storeRequestDto, user.getUserId().toString());

        // 카테고리와 지역 설정
        newStore.setCategory(category);  // Category 설정
        newStore.setRegion(region);      // Region 설정

        return storeRepository.save(newStore);
    }




    // 전체 가게 조회
    public List<?> getAllStores(Long userId) {
        User user = getUser(userId);
        List<Store> allStores = user.getRole() == Role.ADMIN ?
                storeRepository.findAll() : storeRepository.findByisDeletedFalse();

        return allStores.stream()
                .map(store -> user.getRole() == Role.ADMIN ? new FullStoreResponse(store) : new LimitedStoreResponse(store))
                .collect(Collectors.toList());
    }

    // 가게 상세 정보 조회
    public ResponseEntity<?> getStoreDetails(UUID storeId, Long userId) {
        User user = getUser(userId);
        Store store = getStore(storeId);

        return user.getRole() == Role.ADMIN ?
                ResponseEntity.ok(new FullStoreResponse(store)) :
                ResponseEntity.ok(generateLimitedStoreDetails(store));
    }

    // 가게 수정
    @Transactional
    public void updateStore(UUID storeId, Long userId, StoreRequestDto updatedStore) {
        User user = validateUserWithRoles(userId, Role.ADMIN, Role.OWNER);
        Store existingStore = getStore(storeId);

        populateStoreFields(existingStore, updatedStore, user.getUserId().toString());

        storeRepository.save(existingStore);
    }

    // 가게 삭제 (soft delete 방식)
    @Transactional
    public boolean deleteStore(UUID storeId, Long userId) {
        User user = validateUserWithRoles(userId, Role.ADMIN, Role.OWNER);
        Store store = getStore(storeId);

        store.markAsDeleted(user.getUserId().toString());
        storeRepository.save(store);
        return true;
    }

    // 페이징 및 조건부 정렬 구현
    public Page<?> searchStores(UUID categoryId, String sortField, int page, int size, Long userId) {
        User user = getUser(userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortField).descending());
        Page<Store> stores = storeRepository.findByCategoryId(categoryId, pageable);

        return mapStoreResponse(stores, user.getRole());
    }

    public Page<Store> searchStoresByKeyword(String keyword, String sortField, int page, int size, Long userId) {
        User user = getUser(userId);

        // 정렬 설정
        Sort sort = Sort.by(Sort.Direction.DESC, sortField);
        Pageable pageable = PageRequest.of(page, size, sort);

        return storeRepository.searchStores(keyword, pageable);
    }

    // 유저 조회 메서드
    private User getUser(Long userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // 가게 조회 메서드
    private Store getStore(UUID storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));
    }

    private User validateUserWithRoles(Long userId, Role... roles) {
        User user = getUser(userId); // 유저 정보 조회
        System.out.println("User Role: " + user.getRole());  // role 값 확인

        // ADMIN 권한만 체크
        if (user.getRole() == Role.ADMIN) {
            System.out.println("ADMIN 권한 허용.");
            return user;  // ADMIN 권한을 가진 사용자는 정상 처리
        }

        // ADMIN이 아니면 권한 없음
        System.out.println("권한이 없는 유저입니다.");
        throw new IllegalArgumentException("요청 권한이 없습니다.");
    }


    private Map<String, Object> generateLimitedStoreDetails(Store store) {
        Map<String, Object> filteredStoreDetails = new LinkedHashMap<>();
        filteredStoreDetails.put("storeName", store.getStoreName());
        filteredStoreDetails.put("phone", store.getPhone());
        filteredStoreDetails.put("address", store.getAddress().getAddress());
        filteredStoreDetails.put("addressDetail", store.getAddress().getAddressDetail());
        filteredStoreDetails.put("zipCode", store.getAddress().getZipCode());
        return filteredStoreDetails;
    }

    private Page<?> mapStoreResponse(Page<Store> stores, Role role) {
        return role == Role.ADMIN ?
                stores.map(FullStoreResponse::new) :
                stores.map(LimitedStoreResponse::new);
    }

    private void populateStoreFields(Store store, StoreRequestDto storeRequestDto, String userId) {
        if (storeRequestDto.getStoreName() != null) store.setStoreName(storeRequestDto.getStoreName());
        if (storeRequestDto.getPhone() != null) store.setPhone(storeRequestDto.getPhone());

        // 카테고리 이름이 비어있지 않으면 카테고리 조회 후 설정
        if (storeRequestDto.getCategoryName() != null && !storeRequestDto.getCategoryName().isEmpty()) {
            Category category = categoryRepository.findByName(storeRequestDto.getCategoryName())
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카테고리입니다: " + storeRequestDto.getCategoryName()));
            store.setCategory(category);
        } else {
            throw new IllegalArgumentException("카테고리 이름은 필수입니다.");
        }

        // 주소 처리
        if (storeRequestDto.getAddress() != null) store.setAddress(storeRequestDto.getAddress());

        store.setUpdatedBy(userId);
        store.setUpdatedAt(LocalDateTime.now());
    }

}