package com.example.coupon.application;

import com.example.coupon.domain.model.*;
import com.example.coupon.domain.service.strategy.CouponSolverStrategy;
import com.example.coupon.infrastructure.external.MeliItemInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CouponServiceImplTest {

    private CouponSolverStrategy greedyStrategy;
    private CouponSolverStrategy dynamicStrategy;
    private MeliItemInfoService meliItemInfoService;
    private CouponServiceImpl couponService;

    @BeforeEach
    void setUp() {
        greedyStrategy = mock(CouponSolverStrategy.class);
        dynamicStrategy = mock(CouponSolverStrategy.class);
        meliItemInfoService = mock(MeliItemInfoService.class);

        when(greedyStrategy.name()).thenReturn("greedy");
        when(dynamicStrategy.name()).thenReturn("dynamic");

        couponService = new CouponServiceImpl(List.of(greedyStrategy, dynamicStrategy), meliItemInfoService);
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
    void throwsExceptionWhenAlgorithmIsUnknown() {
        CouponRequest request = new CouponRequest(List.of("A", "B"), new BigDecimal("30.00"));

        assertThatThrownBy(() -> couponService.calculateCoupon(request, "unknown"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid algorithm");
    }
}

