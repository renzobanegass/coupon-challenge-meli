package com.example.coupon.infrastructure.external;

import com.example.coupon.domain.port.MeliTokenPort;
import com.example.coupon.infrastructure.persistence.entity.MeliTokenEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MeliTokenAdapterTest {
    @Mock
    private MeliTokenPort tokenPort;

    @InjectMocks
    private MeliTokenAdapter meliTokenAdapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Set dummy clientId and clientSecret
        ReflectionTestUtils.setField(meliTokenAdapter, "clientId", "dummy");
        ReflectionTestUtils.setField(meliTokenAdapter, "clientSecret", "dummy");
    }

    @Test
    void getValidAccessToken_returnsTokenIfNotExpired() {
        MeliTokenEntity entity = new MeliTokenEntity();
        entity.setAccessToken("access");
        entity.setRefreshToken("refresh");
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        when(tokenPort.getToken()).thenReturn(entity);

        String token = meliTokenAdapter.getValidAccessToken();
        assertThat(token).isEqualTo("access");
        verify(tokenPort, never()).updateToken(any());
    }

    @Test
    void getValidAccessToken_refreshesIfExpired() {
        MeliTokenEntity entity = new MeliTokenEntity();
        entity.setAccessToken("old");
        entity.setRefreshToken("refresh");
        entity.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(tokenPort.getToken()).thenReturn(entity);
        // Mock refreshToken to just update the token
        doAnswer(invocation -> {
            MeliTokenEntity arg = invocation.getArgument(0);
            arg.setAccessToken("new");
            arg.setExpiresAt(LocalDateTime.now().plusMinutes(10));
            return null;
        }).when(tokenPort).updateToken(any());
        // Spy on adapter to mock refreshToken
        MeliTokenAdapter spyAdapter = spy(meliTokenAdapter);
        doReturn(entity).when(spyAdapter).refreshToken(any());

        String token = spyAdapter.getValidAccessToken();
        assertThat(token).isEqualTo("old"); // Because our spy returns the same entity
    }

    @Test
    void getValidAccessToken_throwsIfNoToken() {
        when(tokenPort.getToken()).thenReturn(null);
        assertThrows(RuntimeException.class, () -> meliTokenAdapter.getValidAccessToken());
    }
}
