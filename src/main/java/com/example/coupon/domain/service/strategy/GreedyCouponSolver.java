package com.example.coupon.domain.service.strategy;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.coupon.domain.model.Item;
import com.example.coupon.domain.model.Result;
import com.example.coupon.domain.service.GreedyAlgorithm;

@Component
public class GreedyCouponSolver implements CouponSolverStrategy {

    private final GreedyAlgorithm solver = new GreedyAlgorithm();

    @Override
    public Result solve(List<Item> items, BigDecimal amount) {
        return solver.solve(items, amount);
    }

    @Override
    public String name() {
        return "greedy";
    }
}
