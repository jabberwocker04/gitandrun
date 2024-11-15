package com.sparta.gitandrun.menu.service;

import com.sparta.gitandrun.menu.dto.MenuRequestDto;
import com.sparta.gitandrun.menu.dto.MenuResponseDto;
import com.sparta.gitandrun.menu.entity.Menu;
import com.sparta.gitandrun.menu.repository.MenuRepository;
import com.sparta.gitandrun.store.entity.Store;
import com.sparta.gitandrun.store.repository.StoreRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreRepository storeRepository;

    //CREATE
    public MenuResponseDto createMenu(MenuRequestDto requestDto) {

        //가게 정보 조회 후 가게가 존재하지 않을 경우 예외 발생
        Store store = storeRepository.findById(requestDto.getStoreId())
            .orElseThrow(() -> new IllegalArgumentException("가게를 찾을 수 없습니다."));

        //메뉴에 저장
        Menu menu = menuRepository.save(new Menu(requestDto, store));
        return new MenuResponseDto(menu);
    }

    //UPDATE
    @Transactional
    public MenuResponseDto updateMenu(MenuRequestDto requestDto, UUID menuId) {
        findId(menuId);
        Menu menu = menuRepository.findById(menuId).get();

        menu.update(requestDto);
        return new MenuResponseDto(menu);
    }

    //DELETE
    public void deleteMenu(UUID menuId) {
        findId(menuId);
        menuRepository.deleteById(menuId);
    }

    //READ ( 전체 조회 )
    public List<MenuResponseDto> getAllMenus() {
        List<Menu> menuList = menuRepository.findAll();
        List<MenuResponseDto> responseDtoList = new ArrayList<>();
        for (Menu menu : menuList) {
            responseDtoList.add(new MenuResponseDto(menu));
        }
        return responseDtoList;
    }

    //READ ( 단 건 조회 )
    public MenuResponseDto getOneMenu(UUID menuId) {
        findId(menuId);
        Menu menu = menuRepository.findById(menuId).get();
        MenuResponseDto responseDto = new MenuResponseDto(menu);
        return responseDto;
    }

    public List<MenuResponseDto> getDetailMenu(UUID storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("StoreId를 찾을 수 없습니다."));

        List<MenuResponseDto> responseDtoList = new ArrayList<>();
        List<Menu> menuList = menuRepository.findAllByStoreId(storeId);

        for (Menu menu : menuList) {
            responseDtoList.add(new MenuResponseDto(menu));
        }
        return responseDtoList;
    }

    private void findId(UUID id){
        menuRepository.findById(id).orElseThrow(
            () -> new NullPointerException("해당 Id가 존재하지 않습니다.")
        );
    }

    @Transactional // Paging
    public Page<MenuResponseDto> getAllMenusTest( int page, int size, String sortBy) {
        Sort.Direction direction = Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Menu> menuList;

        menuList = menuRepository.findAll(pageable);

        return menuList.map(MenuResponseDto::new);
    }


}
