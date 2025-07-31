package com.example.coupon.application.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Item stats with favorite count")
public record TopFavoriteItemDto(
    @Schema(description = "Item ID", example = "MLA123456789") String id,
    @Schema(description = "Item title", example = "Wireless Mouse") String title,
    @Schema(description = "Item thumbnail URL", example = "https://example.com/image.jpg") String thumbnail,
    @Schema(description = "Item price", example = "49.99") BigDecimal price,
    @Schema(description = "Total number of favorites", example = "42") long favoritesCount
) {}

