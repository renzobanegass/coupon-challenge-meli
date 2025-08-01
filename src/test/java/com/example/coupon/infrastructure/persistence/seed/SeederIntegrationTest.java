package com.example.coupon.infrastructure.persistence.seed;

import com.example.coupon.AbstractIntegrationTest;
import com.example.coupon.infrastructure.persistence.entity.FavoriteEntity;
import com.example.coupon.infrastructure.persistence.repository.FavoriteRepository;
import com.example.coupon.infrastructure.persistence.repository.ItemRepository;
import com.example.coupon.infrastructure.persistence.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@ActiveProfiles("test")
class SeederIntegrationTest extends AbstractIntegrationTest {

    @Autowired private UserSeeder userSeeder;
    @Autowired private ItemSeeder itemSeeder;
    @Autowired private FavoriteSeeder favoriteSeeder;

    @Autowired private UserRepository userRepository;
    @Autowired private ItemRepository itemRepository;
    @Autowired private FavoriteRepository favoriteRepository;

    @Test
    void seeders_shouldInsertExpectedDataAndRelations() {
        userSeeder.seed();
        itemSeeder.seed();
        favoriteSeeder.seed();

        assertThat(userRepository.count()).isEqualTo(10);
        assertThat(itemRepository.count()).isEqualTo(10_000);
        assertThat(favoriteRepository.count()).isGreaterThan(0);

        List<FavoriteEntity> favorites = favoriteRepository.findAll();
        FavoriteEntity fav = favorites.get(0);

        assertThat(fav.getUser()).isNotNull();
        assertThat(fav.getItem()).isNotNull();
    }
}

