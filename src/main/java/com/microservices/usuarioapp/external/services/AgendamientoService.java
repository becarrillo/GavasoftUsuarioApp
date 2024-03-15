package com.microservices.usuarioapp.external.services;

import com.microservices.usuarioapp.external.models.Agendamiento;
import com.microservices.usuarioapp.repositories.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class AgendamientoService {
    @Autowired
    private IClienteRepository iClienteRepository;

    @Autowired
    private RestTemplate restTemplate;
    /*
        AGENDAMIENTO-APP presente en la url de las peticiones a través de RestTemplate,
        así se registra el microservicio agendamientos en servidor Eureka y éste lo localiza
        para la comunicación desde el presente proyecto
    */
    public Agendamiento save(Agendamiento agendamiento) {
        Agendamiento newAgendamiento;
        if (iClienteRepository.listAll().stream().anyMatch(c -> c.getUsuario_id().equals(agendamiento.getUsuarioClienteId()))) {
            newAgendamiento = restTemplate.postForObject(
                    "http://AGENDAMIENTO-APP/agendamientos/agregar/nuevo",
                    agendamiento,
                    Agendamiento.class
            );
        } else {
            newAgendamiento = null;
        }
        return newAgendamiento;
    }

    public Agendamiento getOneById(String agendamientoId) {
        return restTemplate.getForObject("http://AGENDAMIENTO-APP/agendamientos/consultar/{agendamientoId}", Agendamiento.class, agendamientoId);
    }

    public List<Agendamiento> listAll() {
        return restTemplate.getForObject("http://AGENDAMIENTO-APP/agendamientos", List.class);
    }

    public List<Agendamiento> listByCarritoDeComprasId(String carritoDeComprasId) {
        final String URL = "http://AGENDAMIENTO-APP/agendamientos/carritos-de-compras/{carritoDeComprasId}";
        return restTemplate.getForObject(URL, List.class, carritoDeComprasId);
    }

    public List<Agendamiento> listTomadosByUsuarioClienteId(Short usuarioClienteId) {
        final String URL = "http://AGENDAMIENTO-APP/agendamientos/clientes/{usuarioClienteId}/tomados";
        return restTemplate.getForObject(URL, List.class, usuarioClienteId);
    }


    /* Busca obtener del proyecto Agendamiento todos aquellos que son pagos de un determinado
       cliente por su número de documento, haciendo un mapeo en los dos gestores de bases de
       datos en cada uno de los microservicios (el de los usuarios y el de los agendamientos)
    */
    @SuppressWarnings("unchecked")
    public List<Agendamiento> listByClienteNumDocumento(String numDocumento) {
        final Short usuarioClienteId = iClienteRepository.findUsuarioClienteByNumDocumento(numDocumento).getUsuario_id();

        return restTemplate.getForObject(
                "http://AGENDAMIENTO-APP/agendamientos/clientes/{usuarioClienteId}",
                List.class,
                usuarioClienteId
        );
    }

    public void setEstadoToFacturado(String carritoDeComprasId) {
        final String URL = "http://AGENDAMIENTO-APP/agendamientos/carritos-de-compras/{carritoId}/actualizar-estado/facturado";
        restTemplate.put(URL, this.listByCarritoDeComprasId(carritoDeComprasId), carritoDeComprasId);
    }

    public Agendamiento updateOne(Agendamiento agendamiento) {
        final String agendamientoId = agendamiento.getAgendamientoId();
        return restTemplate.postForObject("http://AGENDAMIENTO-APP/agendamientos/modificar/{agendamientoId}", agendamiento, Agendamiento.class, agendamientoId);
    }

    public String cancelOneById(String agendamientoId) {

        return restTemplate.getForObject("http://AGENDAMIENTO-APP/agendamientos/cancelar/{agendamientoId}", String.class, agendamientoId);
    }

    public Agendamiento cancelOnePaidById(String agendamientoId) {
        return restTemplate.getForObject("http://AGENDAMIENTO-APP/agendamientos/cancelar/pagos/{agendamientoId}", Agendamiento.class, agendamientoId);
    }

    public List<Agendamiento> listByUsuarioClienteId(Short usuarioClienteId) {
        return restTemplate.getForObject("http://AGENDAMIENTO-APP/agendamientos/filtrar-por-cliente/{usuarioClienteId}", List.class, usuarioClienteId);
    }

    /*public void reagendarServicio(String agendamientoId, Agendamiento agendamiento) {
        agendamiento.setAgendamientoId(agendamientoId);

        producer.send("Agendamientos-Queue", "Reagendar Servico" +
                "io");

        log.info("Mensaje '{}' enviado con éxito a AGENDAMIENTO-APP en espera de respuesta");
    }

     */
}
