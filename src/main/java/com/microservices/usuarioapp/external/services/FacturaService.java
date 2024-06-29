package com.microservices.usuarioapp.external.services;

import com.microservices.usuarioapp.external.models.Factura;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class FacturaService {
    @Autowired
    private RestTemplate restTemplate;

    public Factura generate(Factura factura) {
        final String URL = "http://gavasoftcomprasapp.railway.internal:8084/v1/facturas/generar-nueva";
        return restTemplate.postForObject(URL, factura, Factura.class);
    }

    public List<Factura> listPagadasByClienteNumDocumento(String clienteNumDocumento) {
        final String URL = "http://gavasoftcomprasapp.railway.internal:8084/v1/facturas/clientes/{clienteNumDocumento}/pagadas";
        return restTemplate.getForObject(URL, List.class, clienteNumDocumento);
    }
}
