package com.example.coupon.application;

import java.util.List;

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
import com.example.coupon.application.dto.TopFavoriteItemDto;
import com.example.coupon.domain.model.CouponRequest;
import com.example.coupon.domain.model.CouponResponse;
import com.example.coupon.domain.service.CouponService;
import com.example.coupon.domain.service.UserFavoritesService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(
    summary = "Optimize item selection for a coupon",
    description = "Finds the best combination of items that can be bought within a given budget using the specified algorithm.",
    responses = {
        @ApiResponse(responseCode = "200", description = "Optimization successful"),
        @ApiResponse(responseCode = "400", description = "Invalid input data or unsupported algorithm"),
    }
    )
    @PostMapping
    public ResponseEntity<CouponResponse> optimizeCoupon(
        @Parameter(
            description = "Coupon request containing item IDs and available amount",
            required = true
        )
        @Valid @RequestBody CouponRequest request,

        @Parameter(
            description = "Algorithm to use for optimization. Options: 'dp' (dynamic programming), 'greedy'. Defaults to 'dp'.",
            example = "dp"
        )
        @RequestParam(defaultValue = "dp") String algo
    ) {
        CouponResponse response = couponService.calculateCoupon(request, algo);
        return ResponseEntity.ok(response);
    }

    @Operation(
    summary = "Get user's favorite items",
    description = "Returns a paginated list of favorite items for the specified user ID"
    )
    @GetMapping("/users/{userId}/favorites")
    public Page<ItemDto> getUserFavorites(
        @Parameter(
            description = "User ID", 
            example = "1"
        )
        @PathVariable Long userId,

        @Parameter(
            description = "Page number (0-based)", 
            example = "0"
        )
        @RequestParam(
            defaultValue = "0"
        ) int page,

        @Parameter(
            description = "Page size", 
            example = "10"
        )
        @RequestParam(
            defaultValue = "10"
        ) int size
    ) {
        return userFavoritesService.getFavoritesForUser(userId, PageRequest.of(page, size));
    }

    @Operation(
    summary = "Get top 5 most favorited items",
    description = "Returns a list of the top 5 items with the most favorites, ordered by popularity descending."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successful retrieval of item rankings",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopFavoriteItemDto.class)))),
    })
    @GetMapping("/stats")
    public ResponseEntity<List<TopFavoriteItemDto>> getTopFavorites() {
        return ResponseEntity.ok(userFavoritesService.getTopFavoriteItems(PageRequest.of(0, 5)));
    }
}
