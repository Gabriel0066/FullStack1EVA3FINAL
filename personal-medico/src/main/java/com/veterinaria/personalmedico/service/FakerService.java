package com.veterinaria.personalmedico.service;

import com.veterinaria.personalmedico.dto.PersonalDTO;
import com.veterinaria.personalmedico.model.Personal;
import com.veterinaria.personalmedico.repository.PersonalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FakerService {

    private final PersonalRepository personalRepository;
    private final Faker faker = new Faker();

    private static final String[] ROLES = {"Veterinario", "Asistente", "Recepcionista", "Conductor", "Cirujano"};

    @Transactional
    public List<PersonalDTO> seedPersonal(int count) {
        log.info("Generando {} registros de personal con DataFaker", count);

        var personalList = IntStream.range(0, count)
                .mapToObj(i -> Personal.builder()
                        .rol(ROLES[faker.random().nextInt(ROLES.length)])
                        .nombre(faker.name().firstName())
                        .apellido(faker.name().lastName())
                        .rut(generateRut())
                        .correo(faker.internet().emailAddress())
                        .telefono("+569" + faker.number().digits(8))
                        .direccion(faker.address().streetAddress())
                        .build())
                .toList();

        var saved = personalRepository.saveAll(personalList);
        log.info("{} registros de personal creados exitosamente", saved.size());

        return saved.stream()
                .map(p -> new PersonalDTO(p.getIdTrabajador(), p.getRol(), p.getNombre(),
                        p.getApellido(), p.getRut(), p.getCorreo(), p.getTelefono(), p.getDireccion()))
                .toList();
    }

    private String generateRut() {
        int rutNum = faker.number().numberBetween(1000000, 25000000);
        int m = 0, s = 1;
        for (int r = rutNum; r != 0; r /= 10) {
            s = (s + r % 10 * (9 - m++ % 6)) % 11;
        }
        String dv = s != 0 ? String.valueOf(s - 1) : "K";
        return rutNum + "-" + dv;
    }
}
