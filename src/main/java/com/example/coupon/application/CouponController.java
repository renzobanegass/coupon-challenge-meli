package com.example.coupon.application;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coupon.domain.model.CouponRequest;
import com.example.coupon.domain.model.CouponResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(name = "Coupon", description = "Coupon optimization endpoints")
@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "Optimize item selection", description = "Returns the best combination of items within the amount using the specified algorithm")
    @PostMapping
    public ResponseEntity<CouponResponse> optimizeCoupon(@Valid @RequestBody CouponRequest request,
                                                         @RequestParam(defaultValue = "dp") String algo) {
        CouponResponse response = couponService.calculateCoupon(request, algo);
        return ResponseEntity.ok(response);
    }
}
