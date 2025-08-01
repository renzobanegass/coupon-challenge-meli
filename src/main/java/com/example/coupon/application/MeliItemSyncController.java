package com.example.coupon.application;

import com.example.coupon.domain.service.MeliItemSyncService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class MeliItemSyncController {
    private final MeliItemSyncService meliItemSyncService;

    @Value("${ADMIN_SYNC_SECRET}")
    private String adminSecret;

    public MeliItemSyncController(MeliItemSyncService meliItemSyncService) {
        this.meliItemSyncService = meliItemSyncService;
    }

    @PostMapping("/sync-meli-items")
    public ResponseEntity<String> syncMeliItems(@RequestHeader("X-ADMIN-SECRET") String secret) {
        if (!adminSecret.equals(secret)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid secret");
        }
        int count = meliItemSyncService.syncItems();
        return ResponseEntity.ok("Synced " + count + " items.");
    }
}
