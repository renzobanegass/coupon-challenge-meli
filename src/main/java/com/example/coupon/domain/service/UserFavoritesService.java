package com.example.coupon.domain.service;

import com.example.coupon.application.dto.ItemDto;
import com.example.coupon.application.dto.TopFavoriteItemDto;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserFavoritesService {
    Page<ItemDto> getFavoritesForUser(Long userId, Pageable pageable);
    List<TopFavoriteItemDto> getTopFavoriteItems(Pageable pageable);
}
