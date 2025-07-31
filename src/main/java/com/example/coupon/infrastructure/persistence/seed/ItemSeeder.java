package com.example.coupon.infrastructure.persistence.seed;

import com.example.coupon.infrastructure.persistence.entity.ItemEntity;
import com.example.coupon.infrastructure.persistence.repository.ItemRepository;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Component
public class ItemSeeder {

    private static final Logger logger = LoggerFactory.getLogger(ItemSeeder.class);
    private static final int TOTAL_ITEMS = 10_000;
    private static final int BATCH_SIZE = 1_000;

    private final ItemRepository itemRepository;
    private final Faker faker = new Faker();

    public ItemSeeder(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void seed() {
        logger.info("Seeding {} items...", TOTAL_ITEMS);
        int index = 0;

        while (index < TOTAL_ITEMS) {
            List<ItemEntity> batch = new ArrayList<>();
            int limit = Math.min(BATCH_SIZE, TOTAL_ITEMS - index);

            for (int i = 0; i < limit; i++) {
                ItemEntity item = new ItemEntity();
                item.setId(generateMercadoLibreId(index + i));
                item.setTitle(faker.commerce().productName());
                item.setPrice(generatePrice());
                batch.add(item);
            }

            itemRepository.saveAll(batch);
            index += limit;

            logger.info("Inserted batch up to index {}", index);
        }

        logger.info("✅ Finished seeding {} items", TOTAL_ITEMS);
    }

    private String generateMercadoLibreId(int i) {
        return String.format("MLA%09d", i);
    }

    private BigDecimal generatePrice() {
        double price = 10 + (500 - 10) * faker.random().nextDouble();
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }
}
