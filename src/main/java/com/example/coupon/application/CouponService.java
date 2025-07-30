package com.example.coupon.application;

import com.example.coupon.domain.model.CouponRequest;
import com.example.coupon.domain.model.CouponResponse;

public interface CouponService {
    CouponResponse calculateCoupon(CouponRequest request, String algorithm);
}
