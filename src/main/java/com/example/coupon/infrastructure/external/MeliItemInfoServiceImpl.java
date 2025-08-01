package com.example.coupon.infrastructure.external;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MeliItemInfoServiceImpl implements MeliItemInfoService {
    private static final Logger log = LoggerFactory.getLogger(MeliItemInfoServiceImpl.class);
    private final MeliTokenAdapter meliTokenAdapter;
    private final MeliItemInfoHttpClient httpClient;

    @Value("${MERCADOLIBRE_ITEMS_ATTRIBUTES:id,price,category_id,title}")
    private String attributes;

    public MeliItemInfoServiceImpl(MeliTokenAdapter meliTokenAdapter, MeliItemInfoHttpClient httpClient) {
        this.meliTokenAdapter = meliTokenAdapter;
        this.httpClient = httpClient;
    }

    @Override
    @CircuitBreaker(name = "meliItems", fallbackMethod = "fallback")
    @Retry(name = "meliItems")
    public List<Map<String, Object>> getItemsInfo(List<String> itemIds) {
        if (itemIds == null || itemIds.isEmpty()) return Collections.emptyList();
        List<List<String>> batches = new ArrayList<>();
        for (int i = 0; i < itemIds.size(); i += 20) {
            batches.add(itemIds.subList(i, Math.min(i + 20, itemIds.size())));
        }
        String accessToken = meliTokenAdapter.getValidAccessToken();
        List<Map<String, Object>> result = new ArrayList<>();
        for (List<String> batch : batches) {
            try {
                List<Map<String, Object>> apiResponse = httpClient.fetchItems(accessToken, attributes, batch);
                if (apiResponse != null) {
                    for (Object obj : apiResponse) {
                        Map<String, Object> item = safeCastAndExtractBody(obj);
                        if (item != null) result.add(item);
                    }
                }
            } catch (Exception ex) {
                log.error("Error calling Mercado Libre items API: {}", ex.getMessage(), ex);
            }
        }
        return result;
    }

    /**
     * Safely cast the object to Map<String, Object> and extract the "body" if code==200.
     */
    private Map<String, Object> safeCastAndExtractBody(Object obj) {
        if (!(obj instanceof Map)) return null;
        Map<String, Object> map = (Map<String, Object>) obj;
        Object codeObj = map.get("code");
        int code = codeObj instanceof Integer ? (Integer) codeObj : 0;
        Object bodyObj = map.get("body");
        if (code == 200 && bodyObj instanceof Map) {
            return (Map<String, Object>) bodyObj;
        }
        return null;
    }

    private List<Map<String, Object>> fallback(List<String> itemIds, Throwable t) {
        log.error("Mercado Libre items API failed: {}", t.getMessage());
        return Collections.emptyList();
    }
}
