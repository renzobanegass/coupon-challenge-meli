package com.example.coupon.infrastructure.persistence.repository;

import com.example.coupon.infrastructure.persistence.entity.MeliTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeliTokenRepository extends JpaRepository<MeliTokenEntity, Long> {
    MeliTokenEntity findTopByOrderByIdAsc();
}
