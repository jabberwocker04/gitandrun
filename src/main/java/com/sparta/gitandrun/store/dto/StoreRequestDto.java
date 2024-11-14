package com.sparta.gitandrun.store.dto;

import com.sparta.gitandrun.store.entity.Address;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StoreRequestDto {

    @NotBlank(message = "가게 이름은 필수 입력 항목입니다.")
    private String storeName;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    private String phone;

    @NotNull(message = "카테고리는 필수 입력 항목입니다.")
    private String categoryName;  // 카테고리 이름을 String으로 받기

    @NotNull(message = "지역 번호는 필수 입력 항목입니다.")
    private Long regionId;

    @NotNull(message = "주소는 필수 입력 항목입니다.")
    private Address address;  // Address 임베디드 클래스를 사용

    // Getter 및 Setter
    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
}
