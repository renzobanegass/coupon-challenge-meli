package com.example.coupon.application;

import com.example.coupon.domain.model.*;
import com.example.coupon.domain.service.strategy.CouponSolverStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CouponServiceImplTest {

    private CouponSolverStrategy greedyStrategy;
    private CouponSolverStrategy dynamicStrategy;
    private CouponServiceImpl couponService;

    @BeforeEach
    void setUp() {
        greedyStrategy = mock(CouponSolverStrategy.class);
        dynamicStrategy = mock(CouponSolverStrategy.class);

        when(greedyStrategy.name()).thenReturn("greedy");
        when(dynamicStrategy.name()).thenReturn("dynamic");

        // simulate Spring injecting both strategies
        couponService = new CouponServiceImpl(List.of(greedyStrategy, dynamicStrategy));
    }

    @Test
    void usesGreedyAlgorithmWhenSpecified() {
        CouponRequest request = new CouponRequest(List.of("X", "Y"), new BigDecimal("20.00"));
        Result result = new Result(List.of("X"), new BigDecimal("15.00"));
        when(greedyStrategy.solve(any(), eq(new BigDecimal("20.00")))).thenReturn(result);

        CouponResponse response = couponService.calculateCoupon(request, "greedy");

        assertThat(response.item_ids()).containsExactly("X");
        assertThat(response.total()).isEqualByComparingTo("15.00");
        assertThat(response.algorithm()).isEqualTo("greedy");
        verify(greedyStrategy).solve(any(), eq(new BigDecimal("20.00")));
    }

    @Test
    void fallsBackToDynamicWhenAlgorithmIsUnknown() {
        CouponRequest request = new CouponRequest(List.of("A", "B"), new BigDecimal("30.00"));
        Result result = new Result(List.of("A"), new BigDecimal("10.00"));
        when(dynamicStrategy.solve(any(), any())).thenReturn(result);

        CouponResponse response = couponService.calculateCoupon(request, "unknown");

        assertThat(response.algorithm()).isEqualTo("dynamic");
    }
}

