package com.example.coupon.domain.service.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.example.coupon.domain.model.Item;
import com.example.coupon.domain.model.Result;

public interface CouponSolverStrategy {
    Result solve(List<Item> items, BigDecimal amount);
    String name();
}
