package com.example.coupon.domain.service.impl;

import com.example.coupon.domain.service.MeliItemSyncService;
import com.example.coupon.infrastructure.external.MeliTokenAdapter;
import com.example.coupon.infrastructure.persistence.entity.ItemEntity;
import com.example.coupon.infrastructure.persistence.repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class MeliItemSyncServiceImpl implements MeliItemSyncService {
    private static final Logger log = LoggerFactory.getLogger(MeliItemSyncServiceImpl.class);
    private final MeliTokenAdapter meliTokenAdapter;
    private final ItemRepository itemRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${MERCADOLIBRE_USER_ID}")
    private String userId;

    public MeliItemSyncServiceImpl(MeliTokenAdapter meliTokenAdapter, ItemRepository itemRepository) {
        this.meliTokenAdapter = meliTokenAdapter;
        this.itemRepository = itemRepository;
    }

    @Override
    public int syncItems() {
        String accessToken = meliTokenAdapter.getValidAccessToken();
        String scrollId = null;
        int total = 0;
        do {
            String url = "https://api.mercadolibre.com/users/" + userId + "/items/search?search_type=scan" + (scrollId != null ? "&scroll_id=" + scrollId : "");
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, entity, (Class<Map<String, Object>>)(Class<?>)Map.class);
            Map<String, Object> body = response.getBody();
            if (body == null || body.get("results") == null) break;
            @SuppressWarnings("unchecked")
            List<String> itemIds = (List<String>) body.get("results");
            scrollId = (String) body.get("scroll_id");
            if (itemIds == null || itemIds.isEmpty()) break;
            for (String itemId : itemIds) {
                ItemEntity item = itemRepository.findById(itemId).orElseGet(() -> {
                    ItemEntity newItem = new ItemEntity();
                    newItem.setId(itemId);
                    return newItem;
                });
                itemRepository.save(item);
                total++;
            }
        } while (scrollId != null);
        log.info("Synced {} items from Mercado Libre", total);
        return total;
    }
}
