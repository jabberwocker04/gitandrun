package com.sparta.gitandrun.store.dto;

import com.sparta.gitandrun.store.entity.Store;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StoreResponseDto {
    private String storeName;
    private String phone;
    private String updatedBy;
    private String address;
    private String addressDetail;
    private String zipCode;
    private String category;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // 전체 필드를 포함하는 생성자 (Admin용)
    public StoreResponseDto(Store store) {
        this.storeName = store.getStoreName();
        this.phone = store.getPhone();
        this.updatedBy = store.getUpdatedBy();
        this.address = store.getAddress().getAddress();  // Address 임베디드 필드 사용
        this.addressDetail = store.getAddress().getAddressDetail();  // Address 임베디드 필드 사용
        this.zipCode = store.getAddress().getZipCode();  // Address 임베디드 필드 사용
        this.category = store.getCategory().toString();
        this.createdAt = store.getCreatedAt();
        this.updatedAt = store.getUpdatedAt();
        this.deletedAt = store.getDeletedAt();
    }

    // 제한된 필드를 포함하는 생성자 (Owner, Manager, Customer용)
    public StoreResponseDto(String storeName, String phone, String updatedBy, String address, String addressDetail, String zipCode) {
        this.storeName = storeName;
        this.phone = phone;
        this.updatedBy = updatedBy;
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipCode = zipCode;
    }
}
