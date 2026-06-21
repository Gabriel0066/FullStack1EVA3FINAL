package com.veterinaria.deliverymascotas.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests para Traslado (modelo)")
class TrasladoTest {

    @Test
    @DisplayName("Debe crear instancia usando builder y verificar todos los campos")
    void builderAndGetters() {
        Traslado t = Traslado.builder()
                .idTraslado(1L)
                .idPaciente(100L)
                .idTrabajador(5L)
                .direccionHogar("Calle Falsa 123")
                .horaRecogida(LocalTime.of(10, 30))
                .estado("PENDIENTE")
                .build();

        assertEquals(1L, t.getIdTraslado());
        assertEquals(100L, t.getIdPaciente());
        assertEquals(5L, t.getIdTrabajador());
        assertEquals("Calle Falsa 123", t.getDireccionHogar());
        assertEquals(LocalTime.of(10, 30), t.getHoraRecogida());
        assertEquals("PENDIENTE", t.getEstado());
    }

    @Test
    @DisplayName("Debe usar setters y verificar valores actualizados")
    void setters() {
        Traslado t = new Traslado();
        t.setIdTraslado(2L);
        t.setIdPaciente(200L);
        t.setIdTrabajador(10L);
        t.setDireccionHogar("Avenida Siempre Viva 742");
        t.setHoraRecogida(LocalTime.of(15, 0));
        t.setEstado("COMPLETADO");

        assertEquals(2L, t.getIdTraslado());
        assertEquals(200L, t.getIdPaciente());
        assertEquals(10L, t.getIdTrabajador());
        assertEquals("Avenida Siempre Viva 742", t.getDireccionHogar());
        assertEquals(LocalTime.of(15, 0), t.getHoraRecogida());
        assertEquals("COMPLETADO", t.getEstado());
    }

    @Test
    @DisplayName("Debe usar equals y hashCode")
    void equalsAndHashCode() {
        Traslado t1 = Traslado.builder().idTraslado(1L).build();
        Traslado t2 = Traslado.builder().idTraslado(1L).build();
        Traslado t3 = Traslado.builder().idTraslado(2L).build();

        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertNotEquals(t1, t3);
    }

    @Test
    @DisplayName("Debe usar toString")
    void testToString() {
        Traslado t = Traslado.builder().idTraslado(1L).estado("PENDIENTE").build();
        String str = t.toString();
        assertNotNull(str);
        assertTrue(str.contains("idTraslado"));
        assertTrue(str.contains("PENDIENTE"));
    }

    @Test
    @DisplayName("Debe usar all-args constructor")
    void allArgsConstructor() {
        Traslado t = new Traslado(1L, 100L, 5L, "Calle", LocalTime.of(9, 0), "PENDIENTE");
        assertEquals(1L, t.getIdTraslado());
    }

    @Test
    @DisplayName("Debe usar no-args constructor")
    void noArgsConstructor() {
        Traslado t = new Traslado();
        assertNull(t.getIdTraslado());
    }

    @Test
    @DisplayName("Debe usar canEqual")
    void canEqual() {
        Traslado t = new Traslado();
        assertTrue(t.canEqual(new Traslado()));
        assertFalse(t.canEqual("otro"));
    }
}
