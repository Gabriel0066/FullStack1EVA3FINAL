package com.veterinaria.personalmedico.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para Personal (modelo)")
class PersonalTest {

    @Test
    @DisplayName("Debe crear instancia usando builder y verificar todos los campos")
    void builderAndGetters() {
        Personal p = Personal.builder()
                .idTrabajador(1L)
                .rol("Cirujano")
                .nombre("Juan")
                .apellido("Perez")
                .rut("12345678-9")
                .correo("juan@vet.com")
                .telefono("+56912345678")
                .direccion("Calle 123")
                .build();

        assertEquals(1L, p.getIdTrabajador());
        assertEquals("Cirujano", p.getRol());
        assertEquals("Juan", p.getNombre());
        assertEquals("Perez", p.getApellido());
        assertEquals("12345678-9", p.getRut());
        assertEquals("juan@vet.com", p.getCorreo());
        assertEquals("+56912345678", p.getTelefono());
        assertEquals("Calle 123", p.getDireccion());
    }

    @Test
    @DisplayName("Debe usar setters y verificar valores actualizados")
    void setters() {
        Personal p = new Personal();
        p.setIdTrabajador(2L);
        p.setRol("Veterinario");
        p.setNombre("Maria");
        p.setApellido("Gonzalez");
        p.setRut("87654321-1");
        p.setCorreo("maria@vet.com");
        p.setTelefono("+56987654321");
        p.setDireccion("Avenida 456");

        assertEquals(2L, p.getIdTrabajador());
        assertEquals("Veterinario", p.getRol());
        assertEquals("Maria", p.getNombre());
        assertEquals("Gonzalez", p.getApellido());
        assertEquals("87654321-1", p.getRut());
        assertEquals("maria@vet.com", p.getCorreo());
        assertEquals("+56987654321", p.getTelefono());
        assertEquals("Avenida 456", p.getDireccion());
    }

    @Test
    @DisplayName("Debe usar equals y hashCode")
    void equalsAndHashCode() {
        Personal p1 = Personal.builder().idTrabajador(1L).build();
        Personal p2 = Personal.builder().idTrabajador(1L).build();
        Personal p3 = Personal.builder().idTrabajador(2L).build();

        assertEquals(p1, p2);
        assertEquals(p1.hashCode(), p2.hashCode());
        assertNotEquals(p1, p3);
    }

    @Test
    @DisplayName("Debe usar toString")
    void testToString() {
        Personal p = Personal.builder().idTrabajador(1L).nombre("Juan").build();
        String str = p.toString();
        assertNotNull(str);
        assertTrue(str.contains("idTrabajador"));
        assertTrue(str.contains("Juan"));
    }

    @Test
    @DisplayName("Debe usar all-args constructor")
    void allArgsConstructor() {
        Personal p = new Personal(1L, "Rol", "Nom", "Ape", "12345678-9",
                "correo@test.com", "+56912345678", "Dir");
        assertEquals(1L, p.getIdTrabajador());
    }

    @Test
    @DisplayName("Debe usar no-args constructor")
    void noArgsConstructor() {
        Personal p = new Personal();
        assertNull(p.getIdTrabajador());
    }

    @Test
    @DisplayName("Debe usar canEqual")
    void canEqual() {
        Personal p = new Personal();
        assertTrue(p.canEqual(new Personal()));
        assertFalse(p.canEqual("otro"));
    }
}
