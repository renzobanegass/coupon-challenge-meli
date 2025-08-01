package com.example.coupon.infrastructure.external;

import java.util.List;
import java.util.Map;

public interface MeliItemInfoService {
    /**
     * Fetches item info for up to 20 item IDs from Mercado Libre.
     * Returns a list of item info maps (id, price, title, etc.).
     * Invalid or missing items are ignored.
     */
    List<Map<String, Object>> getItemsInfo(List<String> itemIds);
}
