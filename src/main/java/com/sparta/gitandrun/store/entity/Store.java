package com.sparta.gitandrun.store.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sparta.gitandrun.category.entity.Category;
import com.sparta.gitandrun.region.entity.Region;
import com.sparta.gitandrun.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_store")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Store {

    @Id
    @Column(name = "store_id", columnDefinition = "UUID")
    private UUID storeId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)  // FK로 `category_id`를 연결
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id", nullable = false) // FK 관계로 `region_id` 추가
    private Region region;

    @Column(name = "store_name", nullable = false, length = 100, unique = true)
    private String storeName;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_by", nullable = false, length = 100)
    private String updatedBy;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "deleted_by", length = 100)
    private String deletedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    // Address 임베디드 클래스 적용
    @Embedded
    private Address address;

    // User와의 ManyToOne 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference  // 순환 참조 방지
    private User user;

    @PrePersist
    public void prePersist() {
        this.storeId = this.storeId == null ? UUID.randomUUID() : this.storeId;
        this.createdAt = this.createdAt == null ? LocalDateTime.now() : this.createdAt;
        this.updatedAt = this.updatedAt == null ? LocalDateTime.now() : this.updatedAt;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDeleted(String deletedBy) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
