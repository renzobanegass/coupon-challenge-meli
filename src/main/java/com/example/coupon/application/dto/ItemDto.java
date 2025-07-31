package com.example.coupon.application.dto;

import java.math.BigDecimal;

public record ItemDto(
    String id,
    String title,
    BigDecimal price,
    String thumbnail
) {}
