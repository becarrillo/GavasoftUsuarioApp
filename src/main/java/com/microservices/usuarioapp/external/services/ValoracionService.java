package com.microservices.usuarioapp.external.services;

import com.microservices.usuarioapp.external.models.Valoracion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class ValoracionService {
    @Autowired
    private RestTemplate restTemplate;

    public String save(String servicioId, Valoracion valoracion) {
        return Objects.requireNonNull(restTemplate.postForObject(
                "http://servicio-app.railway.internal:8082/v1/valoraciones/servicios/{servicioId}/agregar/nueva",
                valoracion,
                Valoracion.class,
                servicioId
        )).toString();
    }

    public String deleteOneById(String servicioValorarionId) {
        return restTemplate.getForObject(
                "http://servicio-app.railway.internal:8082/v1/valoraciones/{servicioValoracionId}/borrar",
                String.class,
                servicioValorarionId
        );
    }
}
