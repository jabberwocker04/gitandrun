package com.sparta.gitandrun.user.repository;

import com.sparta.gitandrun.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByPhone(String phone);

    @Query("select u from User u where u.isDeleted = false")
    List<User> findAllActiveUsers();

    @Query("select u from User u where u.isDeleted = false and u.phone = :phone")
    Optional<User> findActiveUserByPhone(@Param("phone") String phone);
}
