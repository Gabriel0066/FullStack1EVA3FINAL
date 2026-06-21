package com.veterinaria.deliverymascotas.service;

import com.veterinaria.deliverymascotas.model.Traslado;
import com.veterinaria.deliverymascotas.repository.TrasladoRepository;
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
@DisplayName("Tests para FakerService (Delivery Mascotas)")
class FakerServiceTest {

    @Mock
    private TrasladoRepository trasladoRepository;

    private FakerService fakerService;

    @BeforeEach
    void setUp() {
        fakerService = new FakerService(trasladoRepository);
    }

    @Test
    @DisplayName("Debe generar y guardar N traslados falsos")
    void seedTraslados() {
        when(trasladoRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Traslado> result = fakerService.seedTraslados(5);

        assertNotNull(result);
        assertEquals(5, result.size());
        result.forEach(t -> {
            assertNotNull(t.getIdPaciente());
            assertNotNull(t.getIdTrabajador());
            assertNotNull(t.getDireccionHogar());
            assertNotNull(t.getHoraRecogida());
            assertNotNull(t.getEstado());
        });
        verify(trasladoRepository, times(1)).saveAll(any());
    }
}
