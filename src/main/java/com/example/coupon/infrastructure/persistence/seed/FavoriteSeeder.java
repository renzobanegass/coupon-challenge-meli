package com.example.coupon.infrastructure.persistence.seed;

import com.example.coupon.infrastructure.persistence.entity.FavoriteEntity;
import com.example.coupon.infrastructure.persistence.entity.ItemEntity;
import com.example.coupon.infrastructure.persistence.entity.UserEntity;
import com.example.coupon.infrastructure.persistence.repository.FavoriteRepository;
import com.example.coupon.infrastructure.persistence.repository.ItemRepository;
import com.example.coupon.infrastructure.persistence.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FavoriteSeeder {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Faker faker = new Faker();

    public FavoriteSeeder(FavoriteRepository favoriteRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public void seed() {
        if (favoriteRepository.count() > 0) return;

        List<UserEntity> users = userRepository.findAll();
        List<ItemEntity> items = itemRepository.findAll();

        List<FavoriteEntity> favorites = new ArrayList<>();

        for (UserEntity user : users) {
            int count = faker.number().numberBetween(10, 2000);
            Set<String> selectedItemIds = new HashSet<>();

            for (int i = 0; i < count; i++) {
                ItemEntity item = items.get(faker.number().numberBetween(0, items.size()));

                if (selectedItemIds.contains(item.getId())) continue;

                FavoriteEntity fav = new FavoriteEntity();
                fav.setUser(user);
                fav.setItem(item);
                favorites.add(fav);

                selectedItemIds.add(item.getId());
            }
        }

        favoriteRepository.saveAll(favorites);
        System.out.printf("✅ Seeded %d favorites%n", favorites.size());
    }
}
