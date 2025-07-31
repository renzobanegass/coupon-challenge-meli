package com.example.coupon.application;

import com.example.coupon.application.dto.ItemDto;
import com.example.coupon.application.dto.TopFavoriteItemDto;
import com.example.coupon.domain.exception.UserNotFoundException;
import com.example.coupon.domain.service.UserFavoritesService;
import com.example.coupon.infrastructure.persistence.repository.FavoriteRepository;
import com.example.coupon.infrastructure.persistence.repository.UserRepository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserFavoritesServiceImpl implements UserFavoritesService {

    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    public UserFavoritesServiceImpl(FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Page<ItemDto> getFavoritesForUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException("User not found");
        }
        return favoriteRepository.findFavoriteItemsByUserId(userId, pageable);
    }

    @Override
    public List<TopFavoriteItemDto> getTopFavoriteItems(Pageable pageable) {
        return favoriteRepository.findTop5FavoriteItems(pageable);
    }
}
