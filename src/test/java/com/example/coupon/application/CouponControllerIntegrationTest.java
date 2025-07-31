package com.example.coupon.application;

import com.example.coupon.domain.model.CouponRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void returnsValidResponseWithDynamicAlgorithm() throws Exception {
        CouponRequest request = new CouponRequest(List.of("A", "B", "C"), new BigDecimal("30.00"));

        mockMvc.perform(post("/coupon?algo=dp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.item_ids").isArray())
            .andExpect(jsonPath("$.total").value("30.00"))
            .andExpect(jsonPath("$.algorithm").value("dynamic"));
    }

    @Test
    void returnsValidResponseWithGreedyAlgorithm() throws Exception {
        CouponRequest request = new CouponRequest(List.of("A", "B", "C"), new BigDecimal("20.00"));

        mockMvc.perform(post("/coupon?algo=greedy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.item_ids").isArray())
            .andExpect(jsonPath("$.algorithm").value("greedy"));
    }

    @Test
    void returnsBadRequestWhenItemIdsAreEmpty() throws Exception {
        CouponRequest request = new CouponRequest(List.of(), new BigDecimal("10.00"));

        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    @Test
    void returnsBadRequestWhenAmountIsZero() throws Exception {
        CouponRequest request = new CouponRequest(List.of("A", "B"), new BigDecimal("0.00"));

        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void defaultsToDynamicAlgorithmWhenAlgoIsMissing() throws Exception {
        CouponRequest request = new CouponRequest(List.of("A", "B", "C"), new BigDecimal("30.00"));

        mockMvc.perform(post("/coupon")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.algorithm").value("dynamic"));
    }

    @Test
    void returnsBadRequestForInvalidAlgorithm() throws Exception {
        CouponRequest request = new CouponRequest(List.of("A", "B"), new BigDecimal("30.00"));

        mockMvc.perform(post("/coupon?algo=invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Invalid algorithm: invalid"));
    }
}
