package com.example.coupon.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.coupon.domain.model.CouponRequest;
import com.example.coupon.domain.model.CouponResponse;
import com.example.coupon.domain.model.Item;
import com.example.coupon.domain.model.Result;
import com.example.coupon.domain.service.CouponService;
import com.example.coupon.domain.service.strategy.CouponSolverStrategy;

@Service
public class CouponServiceImpl implements CouponService {

    private final Map<String, CouponSolverStrategy> strategies;

    public CouponServiceImpl(List<CouponSolverStrategy> strategies) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(CouponSolverStrategy::name, s -> s));
    }

    @Override
    public CouponResponse calculateCoupon(CouponRequest request, String algorithm) {
        // resolve items (for now fake, real API in simulation phase)
        List<Item> items = request.itemIds().stream()
                .map(id -> new Item(id, BigDecimal.TEN)) // placeholder price
                .toList();

        CouponSolverStrategy solver = switch (algorithm.toLowerCase()) {
            case "greedy" -> strategies.get("greedy");
            case "dp" -> strategies.get("dynamic");
            default -> throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        };

        Result result = solver.solve(items, request.amount());
        return new CouponResponse(result.itemIds(), result.total(), solver.name());
    }
}
