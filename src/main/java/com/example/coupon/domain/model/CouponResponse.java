package com.example.coupon.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record CouponResponse(List<String> item_ids, BigDecimal total, String algorithm) {}
