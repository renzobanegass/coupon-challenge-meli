package com.example.coupon.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record Result(List<String> itemIds, BigDecimal total) {}