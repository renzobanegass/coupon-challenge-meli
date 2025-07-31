package com.example.coupon.domain.service;

import com.example.coupon.domain.model.CouponRequest;
import com.example.coupon.domain.model.CouponResponse;

public interface CouponService {
    CouponResponse calculateCoupon(CouponRequest request, String algorithm);
}
