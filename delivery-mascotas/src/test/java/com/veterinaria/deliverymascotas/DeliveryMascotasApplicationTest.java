package com.veterinaria.deliverymascotas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para DeliveryMascotasApplication")
class DeliveryMascotasApplicationTest {

    @Test
    @DisplayName("Debe crear instancia de la aplicacion")
    void shouldInstantiate() {
        DeliveryMascotasApplication app = new DeliveryMascotasApplication();
        assertNotNull(app);
    }
}
