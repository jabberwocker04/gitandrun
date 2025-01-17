package com.sparta.gitandrun.menu.repository;

import com.sparta.gitandrun.menu.entity.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;


public interface MenuRepository extends JpaRepository<Menu, UUID> {

    @Query("select m from Menu m where m.id in :menuIds and m.isDeleted = false")
    List<Menu> findByIdsAndIsDeletedFalse(@Param("menuIds") List<UUID> menuIds);

    Page<Menu> findAll(Pageable pageable);

    @Query("select m from Menu m where m.store.storeId =:storeId AND m.isDeleted AND m.isDeleted = false ")
    Page<Menu> findAllByStoreId(@Param("storeId") UUID storeId, Pageable pageable);

    @Query("select m from Menu m where m.store.storeId =:storeId AND m.isDeleted AND m.isDeleted = true ")
    Page<Menu> findAllByStoreIdDeleted(@Param("storeId") UUID storeId, Pageable pageable);

    @Query("SELECT m FROM Menu m JOIN m.store s WHERE s.isDeleted = false AND m.isDeleted = false AND s.storeName LIKE %:storeName%")
    Page<Menu> findMenusByStoreName(@Param("storeName") String storeName, Pageable pageable);
}
