package com.example.coupon.domain.service.strategy;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.coupon.domain.model.Item;
import com.example.coupon.domain.model.Result;
import com.example.coupon.domain.service.DynamicProgrammingAlgorithm;

@Component
public class DynamicProgrammingCouponSolver implements CouponSolverStrategy {

    private final DynamicProgrammingAlgorithm solver = new DynamicProgrammingAlgorithm();

    @Override
    public Result solve(List<Item> items, BigDecimal amount) {
        return solver.solve(items, amount);
    }

    @Override
    public String name() {
        return "dynamic";
    }
}
