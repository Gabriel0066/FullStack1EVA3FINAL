package com.veterinaria.deliverymascotas.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para FeignClientConfig")
class FeignClientConfigTest {

    private final FeignClientConfig config = new FeignClientConfig();

    @Test
    @DisplayName("Debe crear interceptor con Basic Auth")
    void basicAuthRequestInterceptor_shouldAddAuthHeader() {
        RequestInterceptor interceptor = config.basicAuthRequestInterceptor();
        assertNotNull(interceptor);

        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        String authHeader = template.headers().get("Authorization").iterator().next();
        assertNotNull(authHeader);
        assertTrue(authHeader.startsWith("Basic "));
    }
}
