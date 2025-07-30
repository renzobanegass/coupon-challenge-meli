package com.example.coupon.domain.model;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record CouponResponse(
    List<String> item_ids,

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    BigDecimal total,

    String algorithm
) {}
