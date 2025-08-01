package com.example.coupon.domain.service;

import org.springframework.stereotype.Service;

@Service
public interface MeliItemSyncService {
    /**
     * Syncs Mercado Libre items to the local database. Returns the number of items upserted.
     */
    int syncItems();
}
