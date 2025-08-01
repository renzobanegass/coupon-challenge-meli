package com.example.coupon.infrastructure.persistence.adapter;

import com.example.coupon.domain.port.MeliTokenPort;
import com.example.coupon.infrastructure.persistence.entity.MeliTokenEntity;
import com.example.coupon.infrastructure.persistence.repository.MeliTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MeliTokenPersistenceAdapter implements MeliTokenPort {
    private final MeliTokenRepository repository;

    public MeliTokenPersistenceAdapter(MeliTokenRepository repository) {
        this.repository = repository;
    }

    @Override
    public MeliTokenEntity getToken() {
        return repository.findTopByOrderByIdAsc();
    }

    @Override
    @Transactional
    public void updateToken(MeliTokenEntity token) {
        repository.save(token);
    }
}
