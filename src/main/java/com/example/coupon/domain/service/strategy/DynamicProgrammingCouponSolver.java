package com.example.coupon.domain.service.strategy;

import java.math.BigDecimal;
import java.util.List;

import com.example.coupon.domain.service.DynamicProgrammingAlgorithm;

public class DynamicProgrammingCouponSolver implements CouponSolverStrategy {

    private final DynamicProgrammingAlgorithm solver = new DynamicProgrammingAlgorithm();

    @Override
    public DynamicProgrammingAlgorithm.Result solve(List<DynamicProgrammingAlgorithm.Item> items, BigDecimal amount) {
        return solver.solve(items, amount);
    }

    @Override
    public String name() {
        return "dynamic";
    }
}
