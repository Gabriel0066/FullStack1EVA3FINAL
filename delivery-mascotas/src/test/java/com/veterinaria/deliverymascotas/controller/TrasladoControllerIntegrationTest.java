package com.veterinaria.deliverymascotas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.deliverymascotas.client.PersonalClient;
import com.veterinaria.deliverymascotas.model.Traslado;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser
@DisplayName("Integración: TrasladoController + DB real")
class TrasladoControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonalClient personalClient;

    @MockBean
    private Flyway flyway;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("POST + GET + DELETE - ciclo de vida completo de un traslado")
    void fullLifecycle() throws Exception {
        when(personalClient.existsById(anyLong())).thenReturn(true);

        var input = Traslado.builder()
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Integración 123")
                .horaRecogida(LocalTime.of(14, 30))
                .estado("PENDIENTE")
                .build();

        String json = objectMapper.writeValueAsString(input);

        // CREATE
        String createdJson = mockMvc.perform(post("/api/v1/traslados")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTraslado").isNumber())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdJson).get("idTraslado").asLong();

        // READ
        mockMvc.perform(get("/api/v1/traslados/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPaciente").value(100));

        // UPDATE ESTADO
        mockMvc.perform(put("/api/v1/traslados/{id}/estado/COMPLETADO", id)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADO"));

        // LIST ALL
        mockMvc.perform(get("/api/v1/traslados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.trasladoList").isArray())
                .andExpect(jsonPath("$._embedded.trasladoList.length()").value(1));

        // DELETE
        mockMvc.perform(delete("/api/v1/traslados/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // VERIFY DELETED
        mockMvc.perform(get("/api/v1/traslados/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/traslados/estadisticas/estado/{estado} - contar por estado")
    void countByEstado() throws Exception {
        when(personalClient.existsById(anyLong())).thenReturn(true);

        Traslado t = Traslado.builder()
                .idPaciente(1L).idTrabajador(1L)
                .direccionHogar("Dir")
                .horaRecogida(LocalTime.of(9, 0))
                .estado("PENDIENTE").build();
        String json = objectMapper.writeValueAsString(t);
        mockMvc.perform(post("/api/v1/traslados").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(json));

        mockMvc.perform(get("/api/v1/traslados/estadisticas/estado/PENDIENTE"))
                .andExpect(status().isOk());
    }
}
