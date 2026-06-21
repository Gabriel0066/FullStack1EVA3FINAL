package com.veterinaria.deliverymascotas.repository;

import com.veterinaria.deliverymascotas.model.Traslado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Integración: TrasladoRepository")
class TrasladoRepositoryIntegrationTest {

    @Autowired
    private TrasladoRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Traslado traslado;

    @BeforeEach
    void setUp() {
        traslado = Traslado.builder()
                .idPaciente(100L)
                .idTrabajador(1L)
                .direccionHogar("Calle Principal 100")
                .horaRecogida(LocalTime.of(9, 0))
                .estado("PENDIENTE")
                .build();
    }

    @Test
    @DisplayName("Debe guardar y recuperar un traslado")
    void saveAndFindById() {
        Traslado saved = entityManager.persistFlushFind(traslado);
        assertNotNull(saved.getIdTraslado());
        assertEquals("PENDIENTE", saved.getEstado());

        Optional<Traslado> found = repository.findById(saved.getIdTraslado());
        assertTrue(found.isPresent());
        assertEquals(saved.getIdTraslado(), found.get().getIdTraslado());
    }

    @Test
    @DisplayName("Debe encontrar por estado")
    void findByEstado() {
        entityManager.persist(traslado);
        entityManager.persist(Traslado.builder()
                .idPaciente(101L).idTrabajador(2L)
                .direccionHogar("Av. Siempre Viva")
                .horaRecogida(LocalTime.of(10, 0))
                .estado("COMPLETADO").build());
        entityManager.flush();

        List<Traslado> pendientes = repository.findByEstado("PENDIENTE");
        assertEquals(1, pendientes.size());

        List<Traslado> todos = repository.findAll();
        assertEquals(2, todos.size());
    }

    @Test
    @DisplayName("Debe encontrar por ID de trabajador")
    void findByIdTrabajador() {
        entityManager.persist(traslado);
        entityManager.persist(Traslado.builder()
                .idPaciente(102L).idTrabajador(1L)
                .direccionHogar("Otra Dir")
                .horaRecogida(LocalTime.of(11, 0))
                .estado("COMPLETADO").build());
        entityManager.flush();

        List<Traslado> result = repository.findByIdTrabajador(1L);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Debe contar por estado")
    void countByEstado() {
        entityManager.persist(traslado);
        entityManager.persist(Traslado.builder()
                .idPaciente(103L).idTrabajador(3L)
                .direccionHogar("Dir 3")
                .horaRecogida(LocalTime.of(12, 0))
                .estado("PENDIENTE").build());
        entityManager.persist(Traslado.builder()
                .idPaciente(104L).idTrabajador(4L)
                .direccionHogar("Dir 4")
                .horaRecogida(LocalTime.of(13, 0))
                .estado("COMPLETADO").build());
        entityManager.flush();

        assertEquals(2, repository.countByEstado("PENDIENTE"));
        assertEquals(1, repository.countByEstado("COMPLETADO"));
    }

    @Test
    @DisplayName("Debe contar por trabajador y estado")
    void countByIdTrabajadorAndEstado() {
        entityManager.persist(traslado);
        entityManager.persist(Traslado.builder()
                .idPaciente(105L).idTrabajador(1L)
                .direccionHogar("Dir 5")
                .horaRecogida(LocalTime.of(14, 0))
                .estado("COMPLETADO").build());
        entityManager.flush();

        assertEquals(1, repository.countByIdTrabajadorAndEstado(1L, "PENDIENTE"));
        assertEquals(1, repository.countByIdTrabajadorAndEstado(1L, "COMPLETADO"));
    }

    @Test
    @DisplayName("Debe eliminar un traslado")
    void deleteById() {
        Traslado saved = entityManager.persistFlushFind(traslado);
        assertTrue(repository.findById(saved.getIdTraslado()).isPresent());

        repository.deleteById(saved.getIdTraslado());
        assertTrue(repository.findById(saved.getIdTraslado()).isEmpty());
    }

    @Test
    @DisplayName("Debe encontrar por estado y trabajador")
    void findByEstadoAndIdTrabajador() {
        entityManager.persist(traslado);
        entityManager.persist(Traslado.builder()
                .idPaciente(106L).idTrabajador(2L)
                .direccionHogar("Dir 6")
                .horaRecogida(LocalTime.of(15, 0))
                .estado("PENDIENTE").build());
        entityManager.flush();

        List<Traslado> result = repository.findByEstadoAndIdTrabajador("PENDIENTE", 1L);
        assertEquals(1, result.size());
        assertEquals(100L, result.get(0).getIdPaciente());
    }
}
