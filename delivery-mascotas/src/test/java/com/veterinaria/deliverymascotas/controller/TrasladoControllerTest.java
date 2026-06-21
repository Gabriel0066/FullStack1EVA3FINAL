package com.veterinaria.deliverymascotas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.deliverymascotas.model.Traslado;
import com.veterinaria.deliverymascotas.service.FakerService;
import com.veterinaria.deliverymascotas.service.TrasladoService;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TrasladoController.class)
@WithMockUser
@DisplayName("Tests para TrasladoController")
class TrasladoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TrasladoService trasladoService;

    @MockBean
    private FakerService fakerService;

    @MockBean
    private Flyway flyway;

    private Traslado createTraslado(Long id) {
        return Traslado.builder()
                .idTraslado(id)
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado("PENDIENTE")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/traslados - debe retornar lista de traslados")
    void getAllTraslados() throws Exception {
        var traslados = List.of(createTraslado(1L), createTraslado(2L));
        when(trasladoService.findAll()).thenReturn(traslados);

        mockMvc.perform(get("/api/v1/traslados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.trasladoList").isArray())
                .andExpect(jsonPath("$._embedded.trasladoList.length()").value(2))
                .andExpect(jsonPath("$._embedded.trasladoList[0].idTraslado").value(1))
                .andExpect(jsonPath("$._embedded.trasladoList[1].idTraslado").value(2));

        verify(trasladoService).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/traslados/{id} - debe retornar traslado cuando existe")
    void getTrasladoById_Found() throws Exception {
        var traslado = createTraslado(1L);
        when(trasladoService.findById(1L)).thenReturn(Optional.of(traslado));

        mockMvc.perform(get("/api/v1/traslados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTraslado").value(1))
                .andExpect(jsonPath("$.idPaciente").value(100))
                .andExpect(jsonPath("$._links.self").exists());

        verify(trasladoService).findById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/traslados/{id} - debe retornar 404 cuando no existe")
    void getTrasladoById_NotFound() throws Exception {
        when(trasladoService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/traslados/999"))
                .andExpect(status().isNotFound());

        verify(trasladoService).findById(999L);
    }

    @Test
    @DisplayName("POST /api/v1/traslados - debe crear traslado exitosamente")
    void createTraslado_Success() throws Exception {
        var input = createTraslado(null);
        var saved = createTraslado(1L);
        when(trasladoService.save(any(Traslado.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/traslados")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTraslado").value(1))
                .andExpect(jsonPath("$._links.self").exists());

        verify(trasladoService).save(any(Traslado.class));
    }

    @Test
    @DisplayName("POST /api/v1/traslados - debe retornar 400 si trabajador no existe")
    void createTraslado_ValidationError() throws Exception {
        var input = createTraslado(null);
        when(trasladoService.save(any(Traslado.class)))
                .thenThrow(new IllegalArgumentException("ID del trabajador médico no existe"));

        mockMvc.perform(post("/api/v1/traslados")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());

        verify(trasladoService).save(any(Traslado.class));
    }

    @Test
    @DisplayName("PUT /api/v1/traslados/{id}/estado/{nuevoEstado} - debe actualizar estado")
    void updateEstado_Success() throws Exception {
        var updated = createTraslado(1L);
        updated.setEstado("COMPLETADO");
        when(trasladoService.updateEstado(1L, "COMPLETADO")).thenReturn(updated);

        mockMvc.perform(put("/api/v1/traslados/1/estado/COMPLETADO")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));

        verify(trasladoService).updateEstado(1L, "COMPLETADO");
    }

    @Test
    @DisplayName("PUT /api/v1/traslados/{id}/estado/{nuevoEstado} - debe retornar 404 si no existe")
    void updateEstado_NotFound() throws Exception {
        when(trasladoService.updateEstado(999L, "COMPLETADO"))
                .thenThrow(new RuntimeException("Traslado no encontrado"));

        mockMvc.perform(put("/api/v1/traslados/999/estado/COMPLETADO")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(trasladoService).updateEstado(999L, "COMPLETADO");
    }

    @Test
    @DisplayName("DELETE /api/v1/traslados/{id} - debe eliminar traslado")
    void deleteTraslado_Success() throws Exception {
        doNothing().when(trasladoService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/traslados/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(trasladoService).deleteById(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/traslados/{id} - debe retornar 404 si no existe")
    void deleteTraslado_NotFound() throws Exception {
        doThrow(new RuntimeException("Traslado no encontrado"))
                .when(trasladoService).deleteById(999L);

        mockMvc.perform(delete("/api/v1/traslados/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(trasladoService).deleteById(999L);
    }

    @Test
    @DisplayName("POST /api/v1/traslados/migrate - debe ejecutar migraciones Flyway")
    void migrateDatabase() throws Exception {
        var result = mock(MigrateResult.class);
        result.migrationsExecuted = 2;
        doReturn(result).when(flyway).migrate();

        mockMvc.perform(post("/api/v1/traslados/migrate")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/traslados/seed/{count} - debe sembrar datos falsos")
    void seedTraslados() throws Exception {
        var seeded = List.of(createTraslado(1L), createTraslado(2L));
        when(fakerService.seedTraslados(5)).thenReturn(seeded);

        mockMvc.perform(post("/api/v1/traslados/seed/5")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(fakerService).seedTraslados(5);
    }

    @Test
    @DisplayName("GET /api/v1/traslados/estadisticas/estado/{estado} - debe contar por estado")
    void countByEstado() throws Exception {
        when(trasladoService.countByEstado("PENDIENTE")).thenReturn(5L);

        mockMvc.perform(get("/api/v1/traslados/estadisticas/estado/PENDIENTE"))
                .andExpect(status().isOk());

        verify(trasladoService).countByEstado("PENDIENTE");
    }

    @Test
    @DisplayName("GET /api/v1/traslados/estadisticas/trabajador/{id}/estado/{estado} - debe contar")
    void countByTrabajadorAndEstado() throws Exception {
        when(trasladoService.countByIdTrabajadorAndEstado(1L, "COMPLETADO")).thenReturn(3L);

        mockMvc.perform(get("/api/v1/traslados/estadisticas/trabajador/1/estado/COMPLETADO"))
                .andExpect(status().isOk());

        verify(trasladoService).countByIdTrabajadorAndEstado(1L, "COMPLETADO");
    }
}
