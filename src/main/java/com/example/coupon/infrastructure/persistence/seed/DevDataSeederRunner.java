package com.example.coupon.infrastructure.persistence.seed;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevDataSeederRunner implements CommandLineRunner {

    private final UserSeeder userSeeder;
    private final ItemSeeder itemSeeder;
    private final FavoriteSeeder favoriteSeeder;

    public DevDataSeederRunner(UserSeeder userSeeder, ItemSeeder itemSeeder, FavoriteSeeder favoriteSeeder) {
        this.userSeeder = userSeeder;
        this.itemSeeder = itemSeeder;
        this.favoriteSeeder = favoriteSeeder;
    }

    @Override
    public void run(String... args) {
        userSeeder.seed();
        itemSeeder.seed();
        favoriteSeeder.seed();
        System.out.println("Seeding complete (users, items, favorites)");
    }
}