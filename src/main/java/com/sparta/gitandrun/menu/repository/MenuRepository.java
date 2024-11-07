package com.sparta.gitandrun.menu.repository;

import com.sparta.gitandrun.menu.entity.Menu;
import com.sparta.gitandrun.menu.service.MenuService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {


//    List<Menu> findAllBy(Menu menu);

}