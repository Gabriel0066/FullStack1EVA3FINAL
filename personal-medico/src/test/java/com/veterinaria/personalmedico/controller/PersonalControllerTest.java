package com.veterinaria.personalmedico.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veterinaria.personalmedico.dto.PersonalDTO;
import com.veterinaria.personalmedico.service.FakerService;
import com.veterinaria.personalmedico.service.PersonalService;
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

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PersonalController.class)
@WithMockUser
@DisplayName("Tests para PersonalController")
class PersonalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonalService personalService;

    @MockBean
    private FakerService fakerService;

    @MockBean
    private Flyway flyway;

    private PersonalDTO createPersonalDTO(Long id) {
        return PersonalDTO.builder()
                .idTrabajador(id)
                .rol("Cirujano")
                .nombre("Juan")
                .apellido("Perez")
                .rut("12345678-9")
                .correo("juan@vet.com")
                .telefono("+56912345678")
                .direccion("Calle Principal 100")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/personal - debe retornar lista de personal")
    void getAllPersonal() throws Exception {
        var personal = List.of(createPersonalDTO(1L), createPersonalDTO(2L));
        when(personalService.findAll()).thenReturn(personal);

        mockMvc.perform(get("/api/v1/personal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.personalDTOList").isArray())
                .andExpect(jsonPath("$._embedded.personalDTOList.length()").value(2))
                .andExpect(jsonPath("$._embedded.personalDTOList[0].idTrabajador").value(1));

        verify(personalService).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/personal/{id} - debe retornar personal cuando existe")
    void getPersonalById_Found() throws Exception {
        var dto = createPersonalDTO(1L);
        when(personalService.findById(1L)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/v1/personal/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idTrabajador").value(1))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$._links.self").exists());

        verify(personalService).findById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/personal/{id} - debe retornar 404 cuando no existe")
    void getPersonalById_NotFound() throws Exception {
        when(personalService.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/personal/999"))
                .andExpect(status().isNotFound());

        verify(personalService).findById(999L);
    }

    @Test
    @DisplayName("POST /api/v1/personal - debe crear personal exitosamente")
    void createPersonal_Success() throws Exception {
        var input = createPersonalDTO(null);
        input.setIdTrabajador(null);
        var saved = createPersonalDTO(1L);
        when(personalService.save(any(PersonalDTO.class))).thenReturn(saved);

        mockMvc.perform(post("/api/v1/personal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idTrabajador").value(1))
                .andExpect(jsonPath("$._links.self").exists())
                .andExpect(header().exists("Location"));

        verify(personalService).save(any(PersonalDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/personal - debe retornar 400 si hay error de validacion")
    void createPersonal_ValidationError() throws Exception {
        var input = createPersonalDTO(null);
        input.setIdTrabajador(null);
        when(personalService.save(any(PersonalDTO.class)))
                .thenThrow(new IllegalArgumentException("Error de validacion"));

        mockMvc.perform(post("/api/v1/personal")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());

        verify(personalService).save(any(PersonalDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/personal/{id} - debe actualizar personal existente")
    void updatePersonal_Success() throws Exception {
        var input = createPersonalDTO(null);
        var updated = createPersonalDTO(1L);
        updated.setNombre("Juan Actualizado");
        when(personalService.update(anyLong(), any(PersonalDTO.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/personal/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Actualizado"));

        verify(personalService).update(anyLong(), any(PersonalDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/personal/{id} - debe retornar 404 si no existe")
    void updatePersonal_NotFound() throws Exception {
        var input = createPersonalDTO(null);
        when(personalService.update(anyLong(), any(PersonalDTO.class)))
                .thenThrow(new RuntimeException("Personal no encontrado"));

        mockMvc.perform(put("/api/v1/personal/999")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isNotFound());

        verify(personalService).update(anyLong(), any(PersonalDTO.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/personal/{id} - debe eliminar personal")
    void deletePersonal_Success() throws Exception {
        doNothing().when(personalService).deleteById(1L);

        mockMvc.perform(delete("/api/v1/personal/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(personalService).deleteById(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/personal/{id} - debe retornar 404 si no existe")
    void deletePersonal_NotFound() throws Exception {
        doThrow(new RuntimeException("Personal no encontrado"))
                .when(personalService).deleteById(999L);

        mockMvc.perform(delete("/api/v1/personal/999")
                        .with(csrf()))
                .andExpect(status().isNotFound());

        verify(personalService).deleteById(999L);
    }

    @Test
    @DisplayName("POST /api/v1/personal/migrate - debe ejecutar migraciones Flyway")
    void migrateDatabase() throws Exception {
        var result = mock(MigrateResult.class);
        result.migrationsExecuted = 2;
        doReturn(result).when(flyway).migrate();

        mockMvc.perform(post("/api/v1/personal/migrate")
                        .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/personal/seed/{count} - debe sembrar datos falsos")
    void seedPersonal() throws Exception {
        var seeded = List.of(createPersonalDTO(1L), createPersonalDTO(2L));
        when(fakerService.seedPersonal(3)).thenReturn(seeded);

        mockMvc.perform(post("/api/v1/personal/seed/3")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        verify(fakerService).seedPersonal(3);
    }

    @Test
    @DisplayName("GET /api/v1/personal/exists/id/{id} - debe verificar existencia por ID")
    void existsById() throws Exception {
        when(personalService.existsById(1L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/personal/exists/id/1"))
                .andExpect(status().isOk());

        verify(personalService).existsById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/personal/exists/rut/{rut} - debe verificar existencia por RUT")
    void existsByRut() throws Exception {
        when(personalService.existsByRut("12345678-9")).thenReturn(true);

        mockMvc.perform(get("/api/v1/personal/exists/rut/12345678-9"))
                .andExpect(status().isOk());

        verify(personalService).existsByRut("12345678-9");
    }

    @Test
    @DisplayName("GET /api/v1/personal/exists/correo/{correo} - debe verificar existencia por correo")
    void existsByCorreo() throws Exception {
        when(personalService.existsByCorreo("juan@vet.com")).thenReturn(true);

        mockMvc.perform(get("/api/v1/personal/exists/correo/juan@vet.com"))
                .andExpect(status().isOk());

        verify(personalService).existsByCorreo("juan@vet.com");
    }
}
