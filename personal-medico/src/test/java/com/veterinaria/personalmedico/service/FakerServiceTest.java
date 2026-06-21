package com.veterinaria.personalmedico.service;

import com.veterinaria.personalmedico.dto.PersonalDTO;
import com.veterinaria.personalmedico.repository.PersonalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests para FakerService (Personal Medico)")
class FakerServiceTest {

    @Mock
    private PersonalRepository personalRepository;

    private FakerService fakerService;

    @BeforeEach
    void setUp() {
        fakerService = new FakerService(personalRepository);
    }

    @Test
    @DisplayName("Debe generar y guardar N registros de personal falsos")
    void seedPersonal() {
        when(personalRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<PersonalDTO> result = fakerService.seedPersonal(3);

        assertNotNull(result);
        assertEquals(3, result.size());
        result.forEach(p -> {
            assertNotNull(p.getRol());
            assertNotNull(p.getNombre());
            assertNotNull(p.getApellido());
            assertNotNull(p.getRut());
            assertTrue(p.getRut().matches("^[0-9]{7,8}-[0-9Kk]$"));
            assertNotNull(p.getCorreo());
            assertNotNull(p.getTelefono());
            assertNotNull(p.getDireccion());
        });
        verify(personalRepository, times(1)).saveAll(any());
    }
}
