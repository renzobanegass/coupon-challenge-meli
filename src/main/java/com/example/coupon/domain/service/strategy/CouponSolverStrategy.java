package com.example.coupon.domain.service.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.example.coupon.domain.service.DynamicProgrammingAlgorithm.Item;
import com.example.coupon.domain.service.DynamicProgrammingAlgorithm.Result;

public interface CouponSolverStrategy {
    Result solve(List<Item> items, BigDecimal amount);
    String name();
}
