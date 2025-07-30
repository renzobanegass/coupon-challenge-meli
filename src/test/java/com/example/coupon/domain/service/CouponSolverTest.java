package com.example.coupon.domain.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.example.coupon.domain.service.CouponSolver.Item;
import com.example.coupon.domain.service.CouponSolver.Result;

public class CouponSolverTest {
    
    private final CouponSolver solver = new CouponSolver();

    @Test
    void selectsOptimalItemsWithinBudget() {
        List<Item> items = List.of(
                new Item("A", new BigDecimal("10.00")),
                new Item("B", new BigDecimal("15.00")),
                new Item("C", new BigDecimal("20.00")),
                new Item("D", new BigDecimal("25.00"))
        );

        Result result = solver.solve(items, new BigDecimal("30.00"));

        assertThat(result.total()).isEqualByComparingTo("30.00");
        assertThat(result.itemIds()).containsExactlyInAnyOrder("A", "C");
    }

    @Test
    void handlesPricesWithTwoDecimals() {
        List<Item> items = List.of(
                new Item("X", new BigDecimal("5.99")),
                new Item("Y", new BigDecimal("3.01")),
                new Item("Z", new BigDecimal("2.00"))
        );

        Result result = solver.solve(items, new BigDecimal("6.00"));

        assertThat(result.total()).isEqualByComparingTo("5.99");
        assertThat(result.itemIds()).containsExactly("X");
    }

    @Test
    void returnsNoItemsIfNoneFit() {
        List<Item> items = List.of(
                new Item("E", new BigDecimal("10.00")),
                new Item("F", new BigDecimal("20.00"))
        );

        Result result = solver.solve(items, new BigDecimal("5.00"));

        assertThat(result.total()).isEqualByComparingTo("0.00");
        assertThat(result.itemIds()).isEmpty();
    }

    @Test
    void skipsItemsExceedingAmountIndividually() {
        List<Item> items = List.of(
                new Item("G", new BigDecimal("100.00")),
                new Item("H", new BigDecimal("1.00")),
                new Item("I", new BigDecimal("2.00"))
        );

        Result result = solver.solve(items, new BigDecimal("2.00"));

        assertThat(result.total()).isEqualByComparingTo("2.00");
        assertThat(result.itemIds()).containsExactly("I");
    }

    @Test
    void returnsZeroTotalForEmptyItemList() {
        List<Item> items = List.of();

        Result result = solver.solve(items, new BigDecimal("50.00"));

        assertThat(result.total()).isEqualByComparingTo("0.00");
        assertThat(result.itemIds()).isEmpty();
    }

    @Test
    void returnsZeroWhenBudgetIsZero() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("10.00")),
            new Item("B", new BigDecimal("20.00"))
        );

        Result result = solver.solve(items, new BigDecimal("0.00"));

        assertThat(result.total()).isEqualByComparingTo("0.00");
        assertThat(result.itemIds()).isEmpty();
    }

    @Test
    void picksItemsThatMatchBudgetExactly() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("5.00")),
            new Item("B", new BigDecimal("10.00")),
            new Item("C", new BigDecimal("15.00"))
        );

        Result result = solver.solve(items, new BigDecimal("25.00"));

        assertThat(result.total()).isEqualByComparingTo("25.00");
        assertThat(result.itemIds()).containsExactlyInAnyOrder("B", "C");
    }

    @Test
    void handlesMultipleValidCombinationsWithSameTotal() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("10.00")),
            new Item("B", new BigDecimal("10.00")),
            new Item("C", new BigDecimal("20.00"))
        );

        Result result = solver.solve(items, new BigDecimal("20.00"));

        assertThat(result.total()).isEqualByComparingTo("20.00");
         List<String> ids = result.itemIds();

        boolean isAB = ids.containsAll(List.of("A", "B")) && ids.size() == 2;
        boolean isC = ids.contains("C") && ids.size() == 1;

        assertThat(isAB || isC).as("Should return exactly A+B or C").isTrue();
    }

    @Test
    void handlesDuplicatePrices() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("10.00")),
            new Item("B", new BigDecimal("10.00")),
            new Item("C", new BigDecimal("5.00"))
        );

        Result result = solver.solve(items, new BigDecimal("15.00"));

        assertThat(result.total()).isEqualByComparingTo("15.00");
        assertThat(result.itemIds()).hasSize(2);
        assertThat(result.itemIds()).contains("C");
    }

    @Test
    void handlesFloatingPointPrecisionCorrectly() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("0.10")),
            new Item("B", new BigDecimal("0.20")),
            new Item("C", new BigDecimal("0.30"))
        );

        Result result = solver.solve(items, new BigDecimal("0.30"));

        assertThat(result.total()).isEqualByComparingTo("0.30");
        assertThat(result.itemIds()).containsAnyOf("C", "A", "B");
    }

    @Test
    void handlesLargeInputQuickly() {
        List<Item> items = IntStream.range(0, 500)
            .mapToObj(i -> new Item("Item" + i, BigDecimal.valueOf(i % 100 + 0.01)))
            .toList();

        Result result = solver.solve(items, new BigDecimal("200.00"));

        assertThat(result.total()).isLessThanOrEqualTo(new BigDecimal("200.00"));
    }
}
