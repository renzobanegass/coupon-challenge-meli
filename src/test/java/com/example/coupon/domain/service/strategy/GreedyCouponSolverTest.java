package com.example.coupon.domain.service.strategy;

import com.example.coupon.domain.service.DynamicProgrammingAlgorithm.Item;
import com.example.coupon.domain.service.DynamicProgrammingAlgorithm.Result;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GreedyCouponSolverTest {

    private final CouponSolverStrategy solver = new GreedyCouponSolver();

    @Test
    void picksMostExpensiveFirst() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("10.00")),
            new Item("B", new BigDecimal("15.00")),
            new Item("C", new BigDecimal("5.00"))
        );

        Result result = solver.solve(items, new BigDecimal("20.00"));

        assertThat(result.total()).isEqualByComparingTo("20.00");
        assertThat(result.itemIds()).containsExactlyInAnyOrder("B", "C");
    }

    @Test
    void greedyMissesBetterComboDueToExpensiveFirstChoice() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("19.00")), // Greedy picks this
            new Item("B", new BigDecimal("10.00")),
            new Item("C", new BigDecimal("10.00"))
        );

        Result result = solver.solve(items, new BigDecimal("20.00"));

        // Greedy gets 19.00, but B + C = 20.00 would be optimal
        assertThat(result.total()).isEqualByComparingTo("19.00");
        assertThat(result.itemIds()).containsExactly("A");
    }

    @Test
    void returnsNothingIfAllItemsExceedBudget() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("25.00")),
            new Item("B", new BigDecimal("30.00"))
        );

        Result result = solver.solve(items, new BigDecimal("20.00"));

        assertThat(result.total()).isEqualByComparingTo("0.00");
        assertThat(result.itemIds()).isEmpty();
    }

    @Test
    void choosesSingleItemThatFitsExactly() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("10.00")),
            new Item("B", new BigDecimal("20.00"))
        );

        Result result = solver.solve(items, new BigDecimal("10.00"));

        assertThat(result.total()).isEqualByComparingTo("10.00");
        assertThat(result.itemIds()).containsExactly("A");
    }

    @Test
    void handlesTieByOriginalOrder() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("10.00")),
            new Item("B", new BigDecimal("10.00")),
            new Item("C", new BigDecimal("5.00"))
        );

        Result result = solver.solve(items, new BigDecimal("15.00"));

        assertThat(result.total()).isEqualByComparingTo("15.00");
        assertThat(result.itemIds()).hasSize(2);
    }

    @Test
    void greedyFailsToFindBestComboWhenCheaperFirst() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("12.00")),
            new Item("B", new BigDecimal("6.00")),
            new Item("C", new BigDecimal("6.00"))
        );

        Result result = solver.solve(items, new BigDecimal("12.00"));

        assertThat(result.total()).isEqualByComparingTo("12.00");
        assertThat(result.itemIds()).containsExactly("A"); // Greedy will pick A only
    }

    @Test
    void greedyFailsToFillBudgetWithSmallerItems() {
        List<Item> items = List.of(
            new Item("A", new BigDecimal("9.00")),
            new Item("B", new BigDecimal("6.00")),
            new Item("C", new BigDecimal("6.00")),
            new Item("D", new BigDecimal("5.00"))
        );

        Result result = solver.solve(items, new BigDecimal("17.00"));

        // Greedy takes 9.00 + 6.00 = 15.00 (leaves out D)
        // DP could take B + C + D = 17.00
        assertThat(result.total()).isEqualByComparingTo("15.00");
        assertThat(result.itemIds()).doesNotContain("D");
    }

    @Test
    void greedyFailsWhenBalancingMultipleSmallItemsIsBetter() {
        List<Item> items = List.of(
            new Item("X", new BigDecimal("9.99")),
            new Item("Y", new BigDecimal("5.01")),
            new Item("Z", new BigDecimal("5.00"))
        );

        Result result = solver.solve(items, new BigDecimal("10.01"));

        // Greedy picks 9.99, leaves 0.02
        // DP could take Y + Z = 10.01
        assertThat(result.total()).isEqualByComparingTo("9.99");
        assertThat(result.itemIds()).containsExactly("X");
    }
}
