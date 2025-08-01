package com.example.coupon.infrastructure.external;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
public class MeliItemInfoTestConfig {
    @Bean
    public MeliItemInfoHttpClient meliItemInfoHttpClient() {
        return (accessToken, attributes, batch) -> Collections.emptyList();
    }
}
