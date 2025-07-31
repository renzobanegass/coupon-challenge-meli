package com.example.coupon.infrastructure.persistence.repository;

import com.example.coupon.infrastructure.persistence.entity.FavoriteEntity;
import com.example.coupon.infrastructure.persistence.entity.UserEntity;
import com.example.coupon.infrastructure.persistence.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {
    boolean existsByUserAndItem(UserEntity user, ItemEntity item);
}
