package com.example.coupon.domain.service.impl;

import com.example.coupon.infrastructure.external.MeliTokenAdapter;
import com.example.coupon.infrastructure.persistence.entity.ItemEntity;
import com.example.coupon.infrastructure.persistence.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class MeliItemSyncServiceImplTest {
    @Mock
    private MeliTokenAdapter meliTokenAdapter;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private MeliItemSyncServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new MeliItemSyncServiceImpl(meliTokenAdapter, itemRepository);
        // Inject mock RestTemplate
        service.getClass().getDeclaredFields();
        // Use reflection to set the private restTemplate field
        try {
            var field = MeliItemSyncServiceImpl.class.getDeclaredField("restTemplate");
            field.setAccessible(true);
            field.set(service, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Set userId
        try {
            var field = MeliItemSyncServiceImpl.class.getDeclaredField("userId");
            field.setAccessible(true);
            field.set(service, "test-user");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void syncItems_handlesEmptyResults() {
        when(meliTokenAdapter.getValidAccessToken()).thenReturn("token");
        Map<String, Object> body = new HashMap<>();
        body.put("results", Collections.emptyList());
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(Class.class))).thenReturn(response);
        int count = service.syncItems();
        assertThat(count).isZero();
        verify(itemRepository, never()).save(any());
    }

    @SuppressWarnings("unchecked")
    @Test
    void syncItems_savesItemsAndHandlesScroll() {
        when(meliTokenAdapter.getValidAccessToken()).thenReturn("token");
        // First call returns 2 items and a scroll_id
        Map<String, Object> body1 = new HashMap<>();
        body1.put("results", Arrays.asList("id1", "id2"));
        body1.put("scroll_id", "scroll123");
        // Second call returns 1 item, no scroll_id (end)
        Map<String, Object> body2 = new HashMap<>();
        body2.put("results", Arrays.asList("id3"));
        body2.put("scroll_id", null);
        ResponseEntity<Map<String, Object>> response1 = new ResponseEntity<>(body1, HttpStatus.OK);
        ResponseEntity<Map<String, Object>> response2 = new ResponseEntity<>(body2, HttpStatus.OK);
        // Default empty response for any other call
        Map<String, Object> emptyBody = new HashMap<>();
        emptyBody.put("results", Collections.emptyList());
        emptyBody.put("scroll_id", null);
        ResponseEntity<Map<String, Object>> emptyResponse = new ResponseEntity<>(emptyBody, HttpStatus.OK);

        // Use doAnswer to handle all calls
        doAnswer(invocation -> {
            String url = invocation.getArgument(0);
            if (url.contains("scroll_id=scroll123")) {
                return response2;
            } else if (!url.contains("scroll_id")) {
                return response1;
            } else {
                return emptyResponse;
            }
        }).when(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(Class.class));
        when(itemRepository.findById(anyString())).thenReturn(Optional.empty());
        int count = service.syncItems();
        assertThat(count).isEqualTo(3);
        verify(itemRepository, times(3)).save(any(ItemEntity.class));
    }

    @Test
    void syncItems_existingItemIsUpdated() {
        when(meliTokenAdapter.getValidAccessToken()).thenReturn("token");
        Map<String, Object> body = new HashMap<>();
        body.put("results", Arrays.asList("id1"));
        body.put("scroll_id", null);
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(Class.class))).thenReturn(response);
        ItemEntity existing = new ItemEntity();
        existing.setId("id1");
        when(itemRepository.findById("id1")).thenReturn(Optional.of(existing));
        int count = service.syncItems();
        assertThat(count).isEqualTo(1);
        verify(itemRepository).save(existing);
    }

    @Test
    void syncItems_handlesNullBody() {
        when(meliTokenAdapter.getValidAccessToken()).thenReturn("token");
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(null, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(Class.class))).thenReturn(response);
        int count = service.syncItems();
        assertThat(count).isZero();
        verify(itemRepository, never()).save(any());
    }

    @Test
    void syncItems_handlesNullResults() {
        when(meliTokenAdapter.getValidAccessToken()).thenReturn("token");
        Map<String, Object> body = new HashMap<>();
        ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(body, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), any(Class.class))).thenReturn(response);
        int count = service.syncItems();
        assertThat(count).isZero();
        verify(itemRepository, never()).save(any());
    }
}
