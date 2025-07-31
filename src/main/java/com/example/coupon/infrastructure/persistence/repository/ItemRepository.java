package com.example.coupon.infrastructure.persistence.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.coupon.infrastructure.persistence.entity.ItemEntity;

public interface ItemRepository extends JpaRepository<ItemEntity, UUID> {
}
