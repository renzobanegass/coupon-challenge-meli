package com.example.coupon.application;

import com.example.coupon.application.dto.ItemDto;
import com.example.coupon.domain.service.UserFavoritesService;
import com.example.coupon.infrastructure.persistence.repository.FavoriteRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserFavoritesServiceImpl implements UserFavoritesService {

    private final FavoriteRepository favoriteRepository;

    public UserFavoritesServiceImpl(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    @Override
    public Page<ItemDto> getFavoritesForUser(Long userId, Pageable pageable) {
        return favoriteRepository.findFavoriteItemsByUserId(userId, pageable);
    }
}
