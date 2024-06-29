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
        return restTemplate.postForObject("http://gavasoftcomprasapp.railway.internal:8084/v1/carritos-de-compras/crear/nuevo", new Carrito(), String.class);
    }

    public String addSubtotal(String carritoDeComprasId, String servicioId) {  // servicioId es el del agendamiento en el carrito
        final Integer agendamientoSubtotal = restTemplate.getForObject(
                "http://servicio-app.railway.internal:8082/v1/servicios/{servicioId}/precio",
                Integer.class,
                servicioId
        );
        return restTemplate.postForObject(
                "http://gavasoftcomprasapp.railway.internal:8084/v1/carritos-de-compras/{carritoDeComprasId}/agregar/item",
                agendamientoSubtotal.intValue(),
                String.class,
                carritoDeComprasId
        );
    }

    public Carrito getOne(String carritoId) {
        return restTemplate.getForObject("http://gavasoftcomprasapp.railway.internal:8084/v1/carritos-de-compras/{carritoId}", Carrito.class, carritoId);
    }

    public void deleteOne(String carritoId) {
        restTemplate.delete("http://gavasoftcomprasapp.railway.internal:8084/v1/carritos-de-compras/{carritoId}/cancelar", carritoId);
    }
}
