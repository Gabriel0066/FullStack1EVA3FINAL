package com.veterinaria.personalmedico;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para PersonalMedicoApplication")
class PersonalMedicoApplicationTest {

    @Test
    @DisplayName("Debe crear instancia de la aplicacion")
    void shouldInstantiate() {
        PersonalMedicoApplication app = new PersonalMedicoApplication();
        assertNotNull(app);
    }
}
