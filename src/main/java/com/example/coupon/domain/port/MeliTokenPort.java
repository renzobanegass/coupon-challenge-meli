package com.example.coupon.domain.port;

import com.example.coupon.infrastructure.persistence.entity.MeliTokenEntity;

public interface MeliTokenPort {
    MeliTokenEntity getToken();
    void updateToken(MeliTokenEntity token);
}
