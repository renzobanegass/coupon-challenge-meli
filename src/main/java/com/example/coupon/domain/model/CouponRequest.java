package com.example.coupon.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record CouponRequest(List<String> item_ids, BigDecimal amount) {}
