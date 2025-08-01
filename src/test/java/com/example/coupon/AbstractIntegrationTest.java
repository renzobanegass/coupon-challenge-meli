package com.example.coupon;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {TestcontainersInitializer.class})
public abstract class AbstractIntegrationTest {
    // The Testcontainers setup is now handled by the TestcontainersInitializer.
    // This base class remains as a convenient marker for your integration tests.
}