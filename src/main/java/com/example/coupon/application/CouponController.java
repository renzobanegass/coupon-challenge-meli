package com.example.coupon.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.coupon.application.dto.ItemDto;
import com.example.coupon.domain.model.CouponRequest;
import com.example.coupon.domain.model.CouponResponse;
import com.example.coupon.domain.service.CouponService;
import com.example.coupon.domain.service.UserFavoritesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

@Tag(name = "Coupon", description = "Coupon optimization endpoints")
@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponService couponService;
    private final UserFavoritesService userFavoritesService;

    public CouponController(CouponService couponService, UserFavoritesService userFavoritesService) {
        this.couponService = couponService;
        this.userFavoritesService = userFavoritesService;
    }

    @Operation(summary = "Optimize item selection", description = "Returns the best combination of items within the amount using the specified algorithm")
    @PostMapping
    public ResponseEntity<CouponResponse> optimizeCoupon(@Valid @RequestBody CouponRequest request,
                                                         @RequestParam(defaultValue = "dp") String algo) {
        CouponResponse response = couponService.calculateCoupon(request, algo);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/favorites")
    public Page<ItemDto> getUserFavorites(
        @PathVariable Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return userFavoritesService.getFavoritesForUser(userId, PageRequest.of(page, size));
    }
}
