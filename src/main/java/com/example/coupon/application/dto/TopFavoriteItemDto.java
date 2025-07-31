package com.example.coupon.application.dto;

import java.math.BigDecimal;

public record TopFavoriteItemDto(
    String id,
    String title,
    String thumbnail,
    BigDecimal price,
    long favoritesCount
) {}

