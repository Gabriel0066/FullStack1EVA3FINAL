package com.veterinaria.deliverymascotas.client;

import com.veterinaria.deliverymascotas.config.FeignClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "personal-medico-client", url = "${personal-medico.url:http://localhost:8081/api/v1/personal}", configuration = FeignClientConfig.class)
public interface PersonalClient {

    @GetMapping("/exists/id/{id}")
    PersonalExistsResponse existsById(@PathVariable("id") Long id);
}
