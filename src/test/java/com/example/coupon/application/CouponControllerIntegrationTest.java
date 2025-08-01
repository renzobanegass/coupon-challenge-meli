package com.example.coupon.application;

import com.example.coupon.AbstractIntegrationTest;
import com.example.coupon.domain.model.CouponRequest;
import com.example.coupon.infrastructure.persistence.entity.FavoriteEntity;
import com.example.coupon.infrastructure.persistence.entity.ItemEntity;
import com.example.coupon.infrastructure.persistence.entity.UserEntity;
import com.example.coupon.infrastructure.persistence.repository.FavoriteRepository;
import com.example.coupon.infrastructure.persistence.repository.ItemRepository;
import com.example.coupon.infrastructure.persistence.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CouponControllerIntegrationTest extends AbstractIntegrationTest {

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.example.coupon.infrastructure.external.MeliItemInfoService meliItemInfoService;

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

    @AfterEach
    void cleanUp() {
        favoriteRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void returnsValidResponseWithDynamicAlgorithm() throws Exception {
        // Seed items in DB
        ItemEntity itemA = new ItemEntity();
        itemA.setId("A");
        itemA.setTitle("Item A");
        itemA.setPrice(new BigDecimal("10.00"));
        itemA.setThumbnail("thumbA");
        itemRepository.save(itemA);
        ItemEntity itemB = new ItemEntity();
        itemB.setId("B");
        itemB.setTitle("Item B");
        itemB.setPrice(new BigDecimal("10.00"));
        itemB.setThumbnail("thumbB");
        itemRepository.save(itemB);
        ItemEntity itemC = new ItemEntity();
        itemC.setId("C");
        itemC.setTitle("Item C");
        itemC.setPrice(new BigDecimal("10.00"));
        itemC.setThumbnail("thumbC");
        itemRepository.save(itemC);
        // Mock HTTP client to return valid info
        List<Map<String, Object>> apiResponse = List.of(
            Map.of(
                "id", "A",
                "title", "Item A",
                "price", new BigDecimal("10.00"),
                "thumbnail", "thumbA"
            ),
            Map.of(
                "id", "B",
                "title", "Item B",
                "price", new BigDecimal("10.00"),
                "thumbnail", "thumbB"
            ),
            Map.of(
                "id", "C",
                "title", "Item C",
                "price", new BigDecimal("10.00"),
                "thumbnail", "thumbC"
            )
        );
        org.mockito.Mockito.when(meliItemInfoService.getItemsInfo(org.mockito.ArgumentMatchers.anyList())).thenReturn(apiResponse);
        CouponRequest request = new CouponRequest(List.of("A", "B", "C"), new BigDecimal("30.00"));

        var result = mockMvc.perform(post("/coupon?algo=DP")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        System.out.println("DEBUG: /coupon?algo=DP response: " + responseBody);

        mockMvc.perform(post("/coupon?algo=DP")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.item_ids").isArray())
            .andExpect(jsonPath("$.total").value("30.00"))
            .andExpect(jsonPath("$.algorithm").value("dynamic"));
    }

    @Test
    void returnsValidResponseWithGreedyAlgorithm() throws Exception {
        ItemEntity itemA = new ItemEntity();
        itemA.setId("A");
        itemA.setTitle("Item A");
        itemA.setPrice(new BigDecimal("10.00"));
        itemA.setThumbnail("thumbA");
        itemRepository.save(itemA);
        ItemEntity itemB = new ItemEntity();
        itemB.setId("B");
        itemB.setTitle("Item B");
        itemB.setPrice(new BigDecimal("10.00"));
        itemB.setThumbnail("thumbB");
        itemRepository.save(itemB);
        ItemEntity itemC = new ItemEntity();
        itemC.setId("C");
        itemC.setTitle("Item C");
        itemC.setPrice(new BigDecimal("10.00"));
        itemC.setThumbnail("thumbC");
        itemRepository.save(itemC);
        List<Map<String, Object>> apiResponse = List.of(
            Map.of(
                "id", "A",
                "title", "Item A",
                "price", new BigDecimal("10.00"),
                "thumbnail", "thumbA"
            ),
            Map.of(
                "id", "B",
                "title", "Item B",
                "price", new BigDecimal("10.00"),
                "thumbnail", "thumbB"
            ),
            Map.of(
                "id", "C",
                "title", "Item C",
                "price", new BigDecimal("10.00"),
                "thumbnail", "thumbC"
            )
        );
        org.mockito.Mockito.when(meliItemInfoService.getItemsInfo(org.mockito.ArgumentMatchers.anyList())).thenReturn(apiResponse);
        CouponRequest request = new CouponRequest(List.of("A", "B", "C"), new BigDecimal("20.00"));

        mockMvc.perform(post("/coupon?algo=GREEDY")
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
        ItemEntity itemA = new ItemEntity();
        itemA.setId("A");
        itemA.setTitle("Item A");
        itemA.setPrice(new BigDecimal("10.00"));
        itemA.setThumbnail("thumbA");
        itemRepository.save(itemA);
        ItemEntity itemB = new ItemEntity();
        itemB.setId("B");
        itemB.setTitle("Item B");
        itemB.setPrice(new BigDecimal("10.00"));
        itemB.setThumbnail("thumbB");
        itemRepository.save(itemB);
        ItemEntity itemC = new ItemEntity();
        itemC.setId("C");
        itemC.setTitle("Item C");
        itemC.setPrice(new BigDecimal("10.00"));
        itemC.setThumbnail("thumbC");
        itemRepository.save(itemC);
        List<Map<String, Object>> apiResponse = List.of(
            Map.of(
                "id", "A",
                "title", "Item A",
                "price", new BigDecimal("10.00"),
                "thumbnail", "thumbA"
            ),
            Map.of(
                "id", "B",
                "title", "Item B",
                "price", new BigDecimal("10.00"),
                "thumbnail", "thumbB"
            ),
            Map.of(
                "id", "C",
                "title", "Item C",
                "price", new BigDecimal("10.00"),
                "thumbnail", "thumbC"
            )
        );
        org.mockito.Mockito.when(meliItemInfoService.getItemsInfo(org.mockito.ArgumentMatchers.anyList())).thenReturn(apiResponse);
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
            .andExpect(content().string(org.hamcrest.Matchers.containsString("No enum constant com.example.coupon.domain.model.Algorithm.invalid")));
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

    @Test
    void returnsTop5MostFavoritedItemsInOrder() throws Exception {
        UserEntity user = new UserEntity();
        user.setNickname("RankingUser");
        user = userRepository.save(user);

        for (int i = 0; i < 10; i++) {
            ItemEntity item = new ItemEntity();
            item.setId(UUID.randomUUID().toString());
            item.setTitle("Item " + i);
            item.setPrice(BigDecimal.TEN);
            item.setThumbnail("http://img/" + i);
            item = itemRepository.save(item);

            int favoriteCount = 10 - i;
            for (int j = 0; j < favoriteCount; j++) {
                UserEntity u = new UserEntity();
                u.setNickname("U" + i + "_" + j);
                u = userRepository.save(u);
                FavoriteEntity favorite = new FavoriteEntity();
                favorite.setUser(u);
                favorite.setItem(item);
                favoriteRepository.save(favorite);
            }
        }

        mockMvc.perform(get("/coupon/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(5))
            .andExpect(jsonPath("$[0].title").value("Item 0"))
            .andExpect(jsonPath("$[1].title").value("Item 1"))
            .andExpect(jsonPath("$[4].title").value("Item 4"));
    }

    @Test
    void handlesTieInFavoriteCountDeterministically() throws Exception {
        ItemEntity itemA = new ItemEntity();
        itemA.setId(UUID.randomUUID().toString());
        itemA.setTitle("Item A");
        itemA.setPrice(BigDecimal.TEN);
        itemA.setThumbnail("thumbA");
        itemA = itemRepository.save(itemA);

        ItemEntity itemB = new ItemEntity();
        itemB.setId(UUID.randomUUID().toString());
        itemB.setTitle("Item B");
        itemB.setPrice(BigDecimal.TEN);
        itemB.setThumbnail("thumbB");
        itemB = itemRepository.save(itemB);

        for (int i = 0; i < 5; i++) {
            UserEntity u = new UserEntity();
            u.setNickname("TieUser" + i);
            u = userRepository.save(u);
            FavoriteEntity favoriteA = new FavoriteEntity();
            favoriteA.setUser(u);
            favoriteA.setItem(itemA);
            FavoriteEntity favoriteB = new FavoriteEntity();
            favoriteB.setUser(u);
            favoriteB.setItem(itemB);
            
            favoriteRepository.save(favoriteA);
            favoriteRepository.save(favoriteB);
        }

        mockMvc.perform(get("/coupon/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[*].title").value(org.hamcrest.Matchers.containsInAnyOrder("Item A", "Item B")));
    }

    @Test
    void returnsEmptyListWhenNoFavoritesExist() throws Exception {
        mockMvc.perform(get("/coupon/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void returnsAnyFiveWhenAllItemsHaveSameFavoriteCount() throws Exception {
        for (int i = 0; i < 10; i++) {
            ItemEntity item = new ItemEntity();
            item.setId(UUID.randomUUID().toString());
            item.setTitle("EqualItem " + i);
            item.setPrice(BigDecimal.ONE);
            item.setThumbnail("thumb" + i);
            item = itemRepository.save(item);

            UserEntity user = new UserEntity();
            user.setNickname("User" + i);
            user = userRepository.save(user);

            FavoriteEntity favorite = new FavoriteEntity();
            favorite.setUser(user);
            favorite.setItem(item);
            favoriteRepository.save(favorite);
        }

        mockMvc.perform(get("/coupon/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(5));
    }

    @Test
    void ignoresItemsWithNoFavorites() throws Exception {
        for (int i = 0; i < 7; i++) {
            ItemEntity item = new ItemEntity();
            item.setId(UUID.randomUUID().toString());
            item.setTitle("Item " + i);
            item.setPrice(BigDecimal.TEN);
            item.setThumbnail("thumb" + i);
            item = itemRepository.save(item);

            if (i < 3) {
                UserEntity user = new UserEntity();
                user.setNickname("U" + i);
                user = userRepository.save(user);
                FavoriteEntity favorite = new FavoriteEntity();
                favorite.setUser(user);
                favorite.setItem(item);
                favoriteRepository.save(favorite);
            }
        }

        mockMvc.perform(get("/coupon/stats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(3));
    }
}
