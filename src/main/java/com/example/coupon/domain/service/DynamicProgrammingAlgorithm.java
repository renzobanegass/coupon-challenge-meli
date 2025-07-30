package com.example.coupon.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class DynamicProgrammingAlgorithm {
    
    public record Item(String id, BigDecimal price) {}
    
    public record Result(List<String> itemIds, BigDecimal total) {}

    public Result solve(List<Item> items, BigDecimal amount) {
        int scale = 2;
        int capacity = amount.multiply(BigDecimal.valueOf(100)).intValue();

        int n = items.size();
        int[] prices = new int[n];
        for (int i = 0; i < n; i++) {
            prices[i] = items.get(i).price.multiply(BigDecimal.valueOf(100)).intValue();
        }

        boolean[][] dp = new boolean[n + 1][capacity + 1];
        dp[0][0] = true;

        for (int i = 1; i <= n; i++) {
            int price = prices[i - 1];
            for (int j = 0; j <= capacity; j++) {
                dp[i][j] = dp[i - 1][j] || (j >= price && dp[i - 1][j - price]);
            }
        }

        int best = 0;
        for (int j = capacity; j >= 0; j--) {
            if (dp[n][j]) {
                best = j;
                break;
            }
        }

        List<String> selected = new ArrayList<>();
        for (int i = n, j = best; i > 0 && j >= 0; i--) {
            int price = prices[i - 1];
            if (j >= price && dp[i - 1][j - price]) {
                selected.add(items.get(i - 1).id);
                j -= price;
            }
        }

        return new Result(selected, BigDecimal.valueOf(best).divide(BigDecimal.valueOf(100), scale, RoundingMode.HALF_EVEN));
    }
}
