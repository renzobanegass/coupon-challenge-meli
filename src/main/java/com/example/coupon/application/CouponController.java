package com.example.coupon.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coupon.domain.model.CouponRequest;
import com.example.coupon.domain.model.CouponResponse;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public ResponseEntity<CouponResponse> optimizeCoupon(@RequestBody CouponRequest request,
                                                         @RequestParam(defaultValue = "dp") String algo) {
        CouponResponse response = couponService.calculateCoupon(request, algo);
        return ResponseEntity.ok(response);
    }
}
