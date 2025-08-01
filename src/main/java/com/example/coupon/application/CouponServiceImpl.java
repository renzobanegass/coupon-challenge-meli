package com.example.coupon.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.coupon.infrastructure.external.MeliItemInfoService;

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
    private final MeliItemInfoService meliItemInfoService;

    public CouponServiceImpl(List<CouponSolverStrategy> strategies, MeliItemInfoService meliItemInfoService) {
        this.strategies = strategies.stream()
                .collect(Collectors.toMap(CouponSolverStrategy::name, s -> s));
        this.meliItemInfoService = meliItemInfoService;
    }

    @Override
    public CouponResponse calculateCoupon(CouponRequest request, String algorithm) {
        // Resolve real item prices from Mercado Libre
        List<String> itemIds = request.itemIds();
        List<Item> items = itemIds.isEmpty() ? List.of() :
            meliItemInfoService.getItemsInfo(itemIds).stream()
                .filter(map -> map.get("id") != null && map.get("price") != null)
                .map(map -> {
                    Object priceObj = map.get("price");
                    BigDecimal price;
                    if (priceObj instanceof BigDecimal bd) {
                        price = bd.setScale(2, java.math.RoundingMode.HALF_EVEN);
                    } else if (priceObj instanceof Double d) {
                        price = BigDecimal.valueOf(d).setScale(2, java.math.RoundingMode.HALF_EVEN);
                    } else if (priceObj instanceof String s) {
                        price = new BigDecimal(s).setScale(2, java.math.RoundingMode.HALF_EVEN);
                    } else {
                        price = new BigDecimal(priceObj.toString()).setScale(2, java.math.RoundingMode.HALF_EVEN);
                    }
                    return new Item((String) map.get("id"), price);
                })
                .toList();
        System.out.println("DEBUG: Items for DP: " + items);
        System.out.println("DEBUG: Budget for DP: " + request.amount());

        CouponSolverStrategy solver = switch (algorithm.toLowerCase()) {
            case "greedy" -> strategies.get("greedy");
            case "dp" -> strategies.get("dynamic");
            default -> throw new IllegalArgumentException("Invalid algorithm: " + algorithm);
        };

        Result result = solver.solve(items, request.amount());
        return new CouponResponse(result.itemIds(), result.total(), solver.name());
    }
}
