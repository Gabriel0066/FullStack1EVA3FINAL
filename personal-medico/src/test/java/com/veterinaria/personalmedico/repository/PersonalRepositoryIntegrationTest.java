package com.veterinaria.personalmedico.repository;

import com.veterinaria.personalmedico.model.Personal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Integración: PersonalRepository")
class PersonalRepositoryIntegrationTest {

    @Autowired
    private PersonalRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    private Personal personal;

    @BeforeEach
    void setUp() {
        personal = Personal.builder()
                .rol("Cirujano")
                .nombre("Juan")
                .apellido("Perez")
                .rut("12345678-9")
                .correo("juan@vet.com")
                .telefono("+56912345678")
                .direccion("Calle 123")
                .build();
    }

    @Test
    @DisplayName("Debe guardar y recuperar un personal")
    void saveAndFindById() {
        Personal saved = entityManager.persistFlushFind(personal);
        assertNotNull(saved.getIdTrabajador());

        Optional<Personal> found = repository.findById(saved.getIdTrabajador());
        assertTrue(found.isPresent());
        assertEquals("Juan", found.get().getNombre());
    }

    @Test
    @DisplayName("Debe encontrar por RUT")
    void findByRut() {
        entityManager.persistFlushFind(personal);

        Optional<Personal> found = repository.findByRut("12345678-9");
        assertTrue(found.isPresent());
        assertEquals("Juan", found.get().getNombre());
    }

    @Test
    @DisplayName("Debe encontrar por correo")
    void findByCorreo() {
        entityManager.persistFlushFind(personal);

        Optional<Personal> found = repository.findByCorreo("juan@vet.com");
        assertTrue(found.isPresent());
    }

    @Test
    @DisplayName("Debe encontrar por rol")
    void findByRol() {
        entityManager.persist(personal);
        entityManager.persist(Personal.builder()
                .rol("Cirujano").nombre("Maria").apellido("Lopez")
                .rut("87654321-1").correo("maria@vet.com")
                .telefono("+56987654321").direccion("Dir 2").build());
        entityManager.flush();

        List<Personal> cirujanos = repository.findByRol("Cirujano");
        assertEquals(2, cirujanos.size());
    }

    @Test
    @DisplayName("Debe verificar existencia por RUT")
    void existsByRut() {
        entityManager.persistFlushFind(personal);

        assertTrue(repository.existsByRut("12345678-9"));
        assertFalse(repository.existsByRut("99999999-9"));
    }

    @Test
    @DisplayName("Debe verificar existencia por correo")
    void existsByCorreo() {
        entityManager.persistFlushFind(personal);

        assertTrue(repository.existsByCorreo("juan@vet.com"));
        assertFalse(repository.existsByCorreo("noexiste@vet.com"));
    }

    @Test
    @DisplayName("Debe encontrar por nombre o apellido")
    void findByNombreOrApellidoContaining() {
        entityManager.persist(personal);
        entityManager.persist(Personal.builder()
                .rol("Veterinario").nombre("Ana").apellido("Perez")
                .rut("11111111-1").correo("ana@vet.com")
                .telefono("+56911111111").direccion("Dir 3").build());
        entityManager.flush();

        List<Personal> result = repository.findByNombreOrApellidoContaining("Perez", "Perez");
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Debe encontrar por rol y nombre/apellido")
    void findByRolAndNombreOrApellidoContaining() {
        entityManager.persist(personal);
        entityManager.persist(Personal.builder()
                .rol("Cirujano").nombre("Pedro").apellido("Gomez")
                .rut("22222222-2").correo("pedro@vet.com")
                .telefono("+56922222222").direccion("Dir 4").build());
        entityManager.flush();

        List<Personal> result = repository.findByRolAndNombreOrApellidoContaining("Cirujano", "Perez");
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Debe eliminar un personal")
    void deleteById() {
        Personal saved = entityManager.persistFlushFind(personal);
        assertTrue(repository.findById(saved.getIdTrabajador()).isPresent());

        repository.deleteById(saved.getIdTrabajador());
        assertTrue(repository.findById(saved.getIdTrabajador()).isEmpty());
    }
}
