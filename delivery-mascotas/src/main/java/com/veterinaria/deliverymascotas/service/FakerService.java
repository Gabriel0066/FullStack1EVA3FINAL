package com.veterinaria.deliverymascotas.service;

import com.veterinaria.deliverymascotas.model.Traslado;
import com.veterinaria.deliverymascotas.repository.TrasladoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FakerService {

    private final TrasladoRepository trasladoRepository;
    private final Faker faker = new Faker();

    private static final String[] ESTADOS = {"PENDIENTE", "EN_PROGRESO", "COMPLETADO", "CANCELADO"};

    @Transactional
    public List<Traslado> seedTraslados(int count) {
        log.info("Generando {} registros de traslado con DataFaker", count);

        var traslados = IntStream.range(0, count)
                .mapToObj(i -> Traslado.builder()
                        .idPaciente((long) faker.number().numberBetween(1, 500))
                        .idTrabajador((long) faker.number().numberBetween(1, 100))
                        .direccionHogar(faker.address().fullAddress())
                        .horaRecogida(LocalTime.of(faker.number().numberBetween(6, 22),
                                faker.random().nextInt(0, 59)))
                        .estado(ESTADOS[faker.random().nextInt(ESTADOS.length)])
                        .build())
                .toList();

        var saved = trasladoRepository.saveAll(traslados);
        log.info("{} registros de traslado creados exitosamente", saved.size());
        return saved;
    }
}
