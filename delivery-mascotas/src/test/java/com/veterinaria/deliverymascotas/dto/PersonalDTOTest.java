package com.veterinaria.deliverymascotas.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para PersonalDTO (delivery-mascotas)")
class PersonalDTOTest {

    @Test
    @DisplayName("Debe crear DTO usando builder y verificar todos los campos")
    void builderAndGetters() {
        LocalDateTime now = LocalDateTime.of(2026, 6, 21, 12, 0);
        PersonalDTO dto = PersonalDTO.builder()
                .idTrabajador(1L)
                .rol("Cirujano")
                .nombre("Juan")
                .apellido("Perez")
                .rut("12345678-9")
                .correo("juan@vet.com")
                .telefono("+56912345678")
                .direccion("Calle 123")
                .fechaCreacion(now)
                .fechaActualizacion(now)
                .build();

        assertEquals(1L, dto.getIdTrabajador());
        assertEquals("Juan", dto.getNombre());
        assertEquals("Perez", dto.getApellido());
        assertEquals("Cirujano", dto.getRol());
        assertEquals("12345678-9", dto.getRut());
        assertEquals("juan@vet.com", dto.getCorreo());
        assertEquals("+56912345678", dto.getTelefono());
        assertEquals("Calle 123", dto.getDireccion());
        assertEquals(now, dto.getFechaCreacion());
        assertEquals(now, dto.getFechaActualizacion());
    }

    @Test
    @DisplayName("Debe usar setters para modificar campos")
    void setters() {
        PersonalDTO dto = new PersonalDTO();
        dto.setIdTrabajador(2L);
        dto.setNombre("Maria");
        dto.setApellido("Gonzalez");
        dto.setRol("Veterinario");
        dto.setRut("87654321-1");
        dto.setCorreo("maria@vet.com");
        dto.setTelefono("+56987654321");
        dto.setDireccion("Avenida 456");
        dto.setFechaCreacion(LocalDateTime.of(2026, 1, 1, 0, 0));
        dto.setFechaActualizacion(LocalDateTime.of(2026, 6, 1, 0, 0));

        assertEquals(2L, dto.getIdTrabajador());
        assertEquals("Maria", dto.getNombre());
        assertEquals("Gonzalez", dto.getApellido());
        assertEquals("Veterinario", dto.getRol());
        assertEquals("87654321-1", dto.getRut());
        assertEquals("maria@vet.com", dto.getCorreo());
        assertEquals("+56987654321", dto.getTelefono());
        assertEquals("Avenida 456", dto.getDireccion());
        assertNotNull(dto.getFechaCreacion());
        assertNotNull(dto.getFechaActualizacion());
    }

    @Test
    @DisplayName("Debe usar equals y hashCode")
    void equalsAndHashCode() {
        PersonalDTO dto1 = PersonalDTO.builder().idTrabajador(1L).build();
        PersonalDTO dto2 = PersonalDTO.builder().idTrabajador(1L).build();
        PersonalDTO dto3 = PersonalDTO.builder().idTrabajador(2L).build();

        assertEquals(dto1, dto2);
        assertEquals(dto1.hashCode(), dto2.hashCode());
        assertNotEquals(dto1, dto3);
    }

    @Test
    @DisplayName("Debe usar toString")
    void testToString() {
        PersonalDTO dto = PersonalDTO.builder().idTrabajador(1L).nombre("Juan").build();
        String str = dto.toString();
        assertNotNull(str);
        assertTrue(str.contains("idTrabajador"));
        assertTrue(str.contains("Juan"));
    }

    @Test
    @DisplayName("Debe usar all-args constructor")
    void allArgsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        PersonalDTO dto = new PersonalDTO(1L, "Rol", "Nom", "Ape", "12345678-9",
                "correo@test.com", "+56912345678", "Dir", now, now);
        assertEquals(1L, dto.getIdTrabajador());
        assertEquals(now, dto.getFechaCreacion());
    }

    @Test
    @DisplayName("Debe usar no-args constructor")
    void noArgsConstructor() {
        PersonalDTO dto = new PersonalDTO();
        assertNull(dto.getIdTrabajador());
    }

    @Test
    @DisplayName("Debe usar canEqual")
    void canEqual() {
        PersonalDTO dto = new PersonalDTO();
        assertTrue(dto.canEqual(new PersonalDTO()));
        assertFalse(dto.canEqual("otro"));
    }
}
