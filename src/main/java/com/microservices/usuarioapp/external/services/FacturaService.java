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

    public Factura generate(Short usuarioClienteId, Factura factura) {
        final String URL = "http://COMPRAS-APP/facturas/usuarios/clientes/{usuarioClienteId}/generar-nueva";
        return restTemplate.postForObject(URL, factura, Factura.class, usuarioClienteId);
    }

    public List<Factura> listPagadasByClienteNumDocumento(String clienteNumDocumento) {
        final String URL = "http://COMPRAS-APP/facturas/usuarios/clientes/{clienteNumDocumento}";
        return restTemplate.getForObject(URL, List.class, clienteNumDocumento);
    }
}
