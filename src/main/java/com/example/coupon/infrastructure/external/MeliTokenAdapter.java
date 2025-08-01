package com.example.coupon.infrastructure.external;

import com.example.coupon.domain.port.MeliTokenPort;
import com.example.coupon.infrastructure.persistence.entity.MeliTokenEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class MeliTokenAdapter {
    private final MeliTokenPort tokenPort;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${MERCADOLIBRE_CLIENT_ID}")
    private String clientId;

    @Value("${MERCADOLIBRE_CLIENT_SECRET}")
    private String clientSecret;

    public MeliTokenAdapter(MeliTokenPort tokenPort) {
        this.tokenPort = tokenPort;
    }

    public synchronized String getValidAccessToken() {
        MeliTokenEntity token = tokenPort.getToken();
        if (token == null) throw new RuntimeException("No Mercado Libre token found in DB");
        if (token.getExpiresAt().isBefore(LocalDateTime.now().plusMinutes(1))) {
            token = refreshToken(token);
        }
        return token.getAccessToken();
    }

    public MeliTokenEntity refreshToken(MeliTokenEntity token) {
        String url = "https://api.mercadolibre.com/oauth/token";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("client_id", clientId);
        params.put("client_secret", clientSecret);
        params.put("refresh_token", token.getRefreshToken());
        String body = params.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .reduce((a, b) -> a + "&" + b).orElse("");
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
        Map resp = response.getBody();
        token.setAccessToken((String) resp.get("access_token"));
        token.setRefreshToken((String) resp.get("refresh_token"));
        Integer expiresIn = (Integer) resp.get("expires_in");
        token.setExpiresAt(LocalDateTime.now().plusSeconds(expiresIn != null ? expiresIn : 3600));
        tokenPort.updateToken(token);
        return token;
    }
}
