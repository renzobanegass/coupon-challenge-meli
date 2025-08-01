package com.example.coupon.infrastructure.persistence.seed;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.coupon.AbstractIntegrationTest;

@Component
@Profile("test")
public class TestDataSeederRunner extends AbstractIntegrationTest  {

    private final UserSeeder userSeeder;
    private final ItemSeeder itemSeeder;
    private final FavoriteSeeder favoriteSeeder;

    public TestDataSeederRunner(UserSeeder userSeeder, ItemSeeder itemSeeder, FavoriteSeeder favoriteSeeder) {
        this.userSeeder = userSeeder;
        this.itemSeeder = itemSeeder;
        this.favoriteSeeder = favoriteSeeder;
    }

    public void seedAll() {
        userSeeder.seed();
        itemSeeder.seed();
        favoriteSeeder.seed();
    }
}
