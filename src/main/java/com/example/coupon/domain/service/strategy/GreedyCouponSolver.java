package com.example.coupon.domain.service.strategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.example.coupon.domain.model.Item;
import com.example.coupon.domain.model.Result;

public class GreedyCouponSolver implements CouponSolverStrategy {

    @Override
    public Result solve(List<Item> items, BigDecimal amount) {
        List<Item> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparing(Item::price).reversed());

        List<String> selectedIds = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Item item : sorted) {
            if (total.add(item.price()).compareTo(amount) <= 0) {
                selectedIds.add(item.id());
                total = total.add(item.price());
            }
        }

        return new Result(selectedIds, total);
    }

    @Override
    public String name() {
        return "greedy";
    }
}
