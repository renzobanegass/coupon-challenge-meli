package com.example.coupon.infrastructure.external;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import java.util.function.Function;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeliItemInfoServiceImplTest {
    @Mock
    private MeliTokenAdapter meliTokenAdapter;
    @Mock
    private MeliItemInfoHttpClient httpClient;
    private MeliItemInfoServiceImpl service;

    @BeforeEach
    void setUp() {
        // MockitoExtension will auto-initialize mocks
        service = new MeliItemInfoServiceImpl(meliTokenAdapter, httpClient);
        // Set attributes
        try {
            var field = MeliItemInfoServiceImpl.class.getDeclaredField("attributes");
            field.setAccessible(true);
            field.set(service, "id,price,category_id,title");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getItemsInfo_returnsValidItems_skipsInvalid() {
        when(meliTokenAdapter.getValidAccessToken()).thenReturn("token");
        Map<String, Object> validBody = new HashMap<>();
        validBody.put("id", "MLA1");
        validBody.put("price", 100);
        Map<String, Object> invalidBody = new HashMap<>();
        invalidBody.put("id", null);
        Map<String, Object> item1 = Map.of("code", 200, "body", validBody);
        Map<String, Object> item2 = Map.of("code", 404, "body", invalidBody);
        List<Map<String, Object>> apiResponse = List.of(item1, item2);
        when(httpClient.fetchItems(anyString(), anyString(), anyList())).thenReturn(apiResponse);
        List<Map<String, Object>> result = service.getItemsInfo(List.of("MLA1", "MLA2"));
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).get("id")).isEqualTo("MLA1");
        assertThat(result.get(0).get("price")).isEqualTo(100);
    }

    @Test
    void getItemsInfo_emptyInput_returnsEmpty() {
        List<Map<String, Object>> result = service.getItemsInfo(Collections.emptyList());
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void getItemsInfo_nullBody_returnsEmpty() {
        when(meliTokenAdapter.getValidAccessToken()).thenReturn("token");
        when(httpClient.fetchItems(anyString(), anyString(), anyList())).thenReturn(null);
        List<Map<String, Object>> result = service.getItemsInfo(List.of("MLA1"));
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void getItemsInfo_non200Code_skipped() {
        when(meliTokenAdapter.getValidAccessToken()).thenReturn("token");
        Map<String, Object> invalidBody = new HashMap<>();
        invalidBody.put("id", "MLA2");
        Map<String, Object> item = Map.of("code", 404, "body", invalidBody);
        List<Map<String, Object>> apiResponse = List.of(item);
        when(httpClient.fetchItems(anyString(), anyString(), anyList())).thenReturn(apiResponse);
        List<Map<String, Object>> result = service.getItemsInfo(List.of("MLA2"));
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    void getItemsInfo_fallbackOnException() {
        when(meliTokenAdapter.getValidAccessToken()).thenReturn("token");
        when(httpClient.fetchItems(anyString(), anyString(), anyList())).thenThrow(new RuntimeException("API down"));
        // Directly call fallback for coverage
        List<Map<String, Object>> result = service.getItemsInfo(List.of("MLA1"));
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}
