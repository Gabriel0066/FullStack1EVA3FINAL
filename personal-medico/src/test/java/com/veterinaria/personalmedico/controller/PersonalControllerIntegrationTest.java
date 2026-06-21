package com.veterinaria.personalmedico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.personalmedico.dto.PersonalDTO;
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

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@WithMockUser
@DisplayName("Integración: PersonalController + DB real")
class PersonalControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("POST + GET + PUT + DELETE - ciclo de vida completo de un personal")
    void fullLifecycle() throws Exception {
        var input = PersonalDTO.builder()
                .rol("Cirujano")
                .nombre("Carlos")
                .apellido("Mendez")
                .rut("12345678-9")
                .correo("carlos@vet.com")
                .telefono("+56912345678")
                .direccion("Av. Siempre Viva 123")
                .build();

        String json = objectMapper.writeValueAsString(input);

        // CREATE
        String createdJson = mockMvc.perform(post("/api/v1/personal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTrabajador").isNumber())
                .andExpect(jsonPath("$.nombre").value("Carlos"))
                .andReturn().getResponse().getContentAsString();

        Long id = objectMapper.readTree(createdJson).get("idTrabajador").asLong();

        // READ
        mockMvc.perform(get("/api/v1/personal/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rut").value("12345678-9"));

        // UPDATE
        var update = PersonalDTO.builder()
                .rol("Cirujano Mayor")
                .nombre("Carlos Updated")
                .apellido("Mendez")
                .rut("12345678-9")
                .correo("carlos@vet.com")
                .telefono("+56912345678")
                .direccion("Av. Siempre Viva 123")
                .build();

        mockMvc.perform(put("/api/v1/personal/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Carlos Updated"))
                .andExpect(jsonPath("$.rol").value("Cirujano Mayor"));

        // LIST ALL
        mockMvc.perform(get("/api/v1/personal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personalDTOList").isArray())
                .andExpect(jsonPath("$._embedded.personalDTOList.length()").value(1));

        // VERIFY EXISTS BY RUT
        mockMvc.perform(get("/api/v1/personal/exists/rut/12345678-9"))
                .andExpect(status().isOk());

        // VERIFY EXISTS BY CORREO
        mockMvc.perform(get("/api/v1/personal/exists/correo/carlos@vet.com"))
                .andExpect(status().isOk());

        // VERIFY EXISTS BY ID
        mockMvc.perform(get("/api/v1/personal/exists/id/{id}", id))
                .andExpect(status().isOk());

        // DELETE
        mockMvc.perform(delete("/api/v1/personal/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // VERIFY DELETED
        mockMvc.perform(get("/api/v1/personal/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST - debe rechazar personal con RUT duplicado")
    void rejectDuplicateRut() throws Exception {
        var dto = PersonalDTO.builder()
                .rol("Veterinario")
                .nombre("Ana")
                .apellido("Silva")
                .rut("11111111-1")
                .correo("ana@vet.com")
                .telefono("+56911111111")
                .direccion("Dir 1")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/v1/personal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/v1/personal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }
}
