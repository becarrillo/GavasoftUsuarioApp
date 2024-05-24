package com.microservices.usuarioapp.external.services;

import com.microservices.usuarioapp.external.models.Ingreso;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class IngresoService {
    @Autowired
    private RestTemplate restTemplate;

    public List<Ingreso> searchByClienteNumDocumento(String clienteNumDocumento) {
        return restTemplate.getForObject("http://COMPRAS-APP/v1/ingresos/clientes/{clienteNumDocumento}", List.class, clienteNumDocumento);
    }
}
