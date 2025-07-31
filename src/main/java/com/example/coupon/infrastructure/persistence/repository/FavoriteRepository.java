package com.example.coupon.infrastructure.persistence.repository;

import com.example.coupon.application.dto.ItemDto;
import com.example.coupon.application.dto.TopFavoriteItemDto;
import com.example.coupon.infrastructure.persistence.entity.FavoriteEntity;
import com.example.coupon.infrastructure.persistence.entity.UserEntity;
import com.example.coupon.infrastructure.persistence.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    boolean existsByUserAndItem(UserEntity user, ItemEntity item);

    @Query("""
        SELECT new com.example.coupon.application.dto.ItemDto(i.id, i.title, i.price, i.thumbnail)
        FROM FavoriteEntity f
        JOIN f.item i
        WHERE f.user.id = :userId
    """)
    Page<ItemDto> findFavoriteItemsByUserId(Long userId, Pageable pageable);

    @Query("""
        SELECT new com.example.coupon.application.dto.TopFavoriteItemDto(
            i.id, i.title, i.thumbnail, i.price, COUNT(f)
        )
        FROM FavoriteEntity f
        JOIN f.item i
        GROUP BY i.id, i.title, i.thumbnail, i.price
        ORDER BY COUNT(f) DESC
    """)
    List<TopFavoriteItemDto> findTop5FavoriteItems(Pageable pageable);
}
