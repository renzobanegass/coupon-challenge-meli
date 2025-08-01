package com.example.coupon.infrastructure.persistence;

import com.example.coupon.AbstractIntegrationTest;
import com.example.coupon.infrastructure.persistence.entity.MeliTokenEntity;
import com.example.coupon.infrastructure.persistence.repository.MeliTokenRepository;
import com.example.coupon.infrastructure.external.MeliTokenAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MeliTokenIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private MeliTokenRepository meliTokenRepository;

    @MockBean
    private MeliTokenAdapter meliTokenAdapter;

    @Test
    void canSaveAndRetrieveMeliToken() {
        MeliTokenEntity entity = new MeliTokenEntity();
        entity.setAccessToken("test-access-token");
        entity.setRefreshToken("test-refresh-token");
        entity.setExpiresAt(LocalDateTime.now().plusHours(1));

        meliTokenRepository.save(entity);

        MeliTokenEntity found = meliTokenRepository.findTopByOrderByIdAsc();
        assertThat(found).isNotNull();
        assertThat(found.getAccessToken()).isEqualTo("test-access-token");
        assertThat(found.getRefreshToken()).isEqualTo("test-refresh-token");
        assertThat(found.getExpiresAt()).isAfter(LocalDateTime.now());
    }
}
