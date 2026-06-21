package com.veterinaria.deliverymascotas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para OpenApiConfig")
class OpenApiConfigTest {

    @Test
    @DisplayName("Debe crear instancia de configuracion")
    void shouldInstantiate() {
        OpenApiConfig config = new OpenApiConfig();
        assertNotNull(config);
    }
}
