package com.microservices.usuarioapp.external.services;

import com.microservices.usuarioapp.external.models.Carrito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CarritoService {
    @Autowired
    private RestTemplate restTemplate;

    public String create() {
        return restTemplate.postForObject("http://COMPRAS-APP/v1/carritos-de-compras/crear/nuevo", new Carrito(), String.class);
    }

    public String addSubtotal(String carritoDeComprasId, String servicioId) {  // servicioId es el del agendamiento en el carrito
        final Integer agendamientoSubtotal = restTemplate.getForObject(
                "http://COMPRAS-APP/v1/servicios/{servicioId}/precio",
                Integer.class,
                servicioId
        );
        return restTemplate.postForObject(
                "http://COMPRAS-APP/v1/carritos-de-compras/{carritoDeComprasId}/agregar/item",
                agendamientoSubtotal.intValue(),
                String.class,
                carritoDeComprasId
        );
    }

    public Carrito getOne(String carritoId) {
        return restTemplate.getForObject("http://COMPRAS-APP/v1/carritos-de-compras/{carritoId}", Carrito.class, carritoId);
    }
}
