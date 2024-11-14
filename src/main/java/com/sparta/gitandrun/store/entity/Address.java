package com.sparta.gitandrun.store.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Embeddable
@RequiredArgsConstructor
@Getter
public class Address {

    @Column(nullable = false, length = 50)
    private String address;

    @Column(name = "address_detail", length = 50)
    private String addressDetail;

    @Column(name = "zip_code", nullable = false, length = 50)
    private String zipCode;

    @Builder
    public Address(String address, String addressDetail, String zipCode) {
        this.address = address;
        this.addressDetail = addressDetail;
        this.zipCode = zipCode;
    }
}

