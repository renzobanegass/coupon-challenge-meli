package com.example.coupon.infrastructure.external;

import java.util.List;
import java.util.Map;

public interface MeliItemInfoHttpClient {
    List<Map<String, Object>> fetchItems(String accessToken, String attributes, List<String> batch);
}
