package com.example.coupon.application;

import com.example.coupon.domain.model.CouponRequest;
import com.example.coupon.infrastructure.persistence.entity.FavoriteEntity;
import com.example.coupon.infrastructure.persistence.entity.ItemEntity;
import com.example.coupon.infrastructure.persistence.entity.UserEntity;
import com.example.coupon.infrastructure.persistence.repository.FavoriteRepository;
import com.example.coupon.infrastructure.persistence.repository.ItemRepository;
import com.example.coupon.infrastructure.persistence.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CouponControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired 
    private UserRepository userRepository;
    @Autowired 
    private ItemRepository itemRepository;
    @Autowired 
    private FavoriteRepository favoriteRepository;

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

    @Test
    void returnsUserFavoritesWithPagination() throws Exception {
        UserEntity user = new UserEntity();
        user.setNickname("Test User");
        user = userRepository.save(user);

        List<ItemEntity> items = IntStream.range(0, 10)
            .mapToObj(i -> {
                ItemEntity item = new ItemEntity();
                item.setId(UUID.randomUUID().toString());
                item.setTitle("Item " + i);
                item.setPrice(BigDecimal.valueOf(10 + i));
                item.setThumbnail("http://example.com/image" + i + ".jpg");
                return itemRepository.save(item);
            }).toList();

        for (ItemEntity item : items) {
            FavoriteEntity favorite = new FavoriteEntity();
            favorite.setUser(user);
            favorite.setItem(item);
            favoriteRepository.save(favorite);
        }

        mockMvc.perform(get("/coupon/users/{userId}/favorites", user.getId())
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content.length()").value(5))
            .andExpect(jsonPath("$.totalElements").value(10))
            .andExpect(jsonPath("$.totalPages").value(2));
    }
    
    @Test
    void returnsExactlyOneFullPage() throws Exception {
        UserEntity user = new UserEntity();
        user.setNickname("Full Page");
        user = userRepository.save(user);

        for (int i = 0; i < 5; i++) {
            ItemEntity item = new ItemEntity();
            item.setId(UUID.randomUUID().toString());
            item.setTitle("item" + i);
            item.setPrice(BigDecimal.TEN);
            item.setThumbnail("thumb" + i);
            item = itemRepository.save(item);

            FavoriteEntity favorite = new FavoriteEntity();
            favorite.setUser(user);
            favorite.setItem(item);
            favoriteRepository.save(favorite);
        }

        mockMvc.perform(get("/coupon/users/{userId}/favorites", user.getId())
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content.length()").value(5))
            .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void returnsEmptyPageWhenRequestingPageBeyondRange() throws Exception {
        UserEntity user = new UserEntity();
        user.setNickname("Paged User");
        user = userRepository.save(user);

        for (int i = 0; i < 3; i++) {
            ItemEntity item = new ItemEntity();
            item.setId(UUID.randomUUID().toString());
            item.setTitle("item" + i);
            item.setPrice(BigDecimal.TEN);
            item.setThumbnail("thumb" + i);
            item = itemRepository.save(item);

            FavoriteEntity favorite = new FavoriteEntity();
            favorite.setUser(user);
            favorite.setItem(item);
            favoriteRepository.save(favorite);
        }

        mockMvc.perform(get("/coupon/users/{userId}/favorites", user.getId())
                .param("page", "1")
                .param("size", "5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void returnsEmptyPageWhenUserHasNoFavorites() throws Exception {
        UserEntity user = new UserEntity();
        user.setNickname("No Favorites");
        user = userRepository.save(user);

        mockMvc.perform(get("/coupon/users/{userId}/favorites", user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content").isEmpty())
            .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void returnsNotFoundForNonExistentUser() throws Exception {
        mockMvc.perform(get("/coupon/users/{userId}/favorites", 9999L))
            .andExpect(status().isNotFound())
            .andExpect(content().string("User not found"));
    }

    @Test
    void returnsBadRequestForNegativePaginationParameters() throws Exception {
        mockMvc.perform(get("/coupon/users/{userId}/favorites", 1L)
                .param("page", "-1")
                .param("size", "-10"))
            .andExpect(status().isBadRequest());
    }
}
