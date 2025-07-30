package com.example.coupon.domain.model;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CouponRequest(
    @Schema(description = "List of item IDs to consider")
    @NotEmpty(message = "item_ids must not be empty")
    List<String> item_ids,

    @Schema(description = "Total amount of the coupon")
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than 0")
    BigDecimal amount
) {}
