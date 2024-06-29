package com.microservices.usuarioapp.external.services;

import com.microservices.usuarioapp.external.models.Agendamiento;
import com.microservices.usuarioapp.repositories.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@SuppressWarnings("unchecked")
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
        if (
            agendamiento.getUsuarioClienteId()==null || 
            iClienteRepository.listAll().stream()
                .anyMatch(
                    c -> c.getUsuario_id().equals(agendamiento.getUsuarioClienteId())
                )
        ) {
            newAgendamiento = restTemplate.postForObject(
                    "http://agendamiento-app.railway.internal:8083/v1/agendamientos/agregar/nuevo",
                    agendamiento,
                    Agendamiento.class
            );
        } else {
            newAgendamiento = null;
        }
        return newAgendamiento;
    }

    public Agendamiento getOneById(String agendamientoId) {
        return restTemplate.getForObject(
                "http://agendamiento-app.railway.internal:8083/v1/agendamientos/{agendamientoId}",
                Agendamiento.class,
                agendamientoId
        );
    }

    public String getCarritoDeComprasIdByUsuarioClienteId(Short usuarioClienteId) {
        return restTemplate.getForObject(
                "http://agendamiento-app.railway.internal:8083/v1/agendamientos/filtrar-por-cliente/{usuarioClienteId}/carrito-de-compras-id",
                String.class,
                usuarioClienteId
        );
    }

    public List<Agendamiento> listAll() {
        return restTemplate.getForObject("http://agendamiento-app.railway.internal:8083/v1/agendamientos", List.class);
    }

    public List<Agendamiento> listAllByClienteNumDocumento(String numDocumento) {
        final Short usuarioClienteId = iClienteRepository.findUsuarioClienteIdByNumDocumento(numDocumento);

        return restTemplate.getForObject(
                "http://agendamiento-app.railway.internal:8083/v1/agendamientos/filtrar-por-cliente/{usuarioClienteId}",
                List.class,
                usuarioClienteId
        );
    }

    public List<Agendamiento> listAllByCarritoDeComprasId(String carritoDeComprasId) {
        final String URL = "http://agendamiento-app.railway.internal:8083/v1/agendamientos/filtrar-por-carrito-de-compras/{carritoDeComprasId}";
        return restTemplate.getForObject(URL, List.class, carritoDeComprasId);
    }

    public List<Agendamiento> listTomadosByUsuarioClienteId(Short usuarioClienteId) {
        final String URL = "http://agendamiento-app.railway.internal:8083/v1/agendamientos/clientes/{usuarioClienteId}/tomados";
        return restTemplate.getForObject(URL, List.class, usuarioClienteId);
    }


    /* Busca obtener del proyecto Agendamiento todos aquellos que son pagos de un determinado
       cliente por su número de documento, haciendo un mapeo en los dos gestores de bases de
       datos en cada uno de los microservicios (el de los usuarios y el de los agendamientos)
    */
    public List<Agendamiento> listTomadosByClienteNumDocumento(String numDocumento) {
        final Short usuarioClienteId = iClienteRepository.findUsuarioClienteIdByNumDocumento(numDocumento);

        return restTemplate.getForObject(
                "http://agendamiento-app.railway.internal:8083/v1/agendamientos/clientes/{usuarioClienteId}/tomados",
                List.class,
                usuarioClienteId
        );
    }

    public List<Agendamiento> listPagadosByClienteNumDocumento(String numDocumento) {
        final Short usuarioClienteId = iClienteRepository.findUsuarioClienteIdByNumDocumento(numDocumento);

        return restTemplate.getForObject(
                "http://agendamiento-app.railway.internal:8083/v1/agendamientos/clientes/{usuarioClienteId}/pagados",
                List.class,
                usuarioClienteId
        );
    }

    public List<Agendamiento> setEstadoToFacturado(String carritoDeComprasId) {
        final String URL = "http://agendamiento-app.railway.internal:8083/v1/agendamientos/carritos-de-compras/{carritoDeComprasId}/actualizar-estado/facturado";
        return restTemplate.getForObject(URL, List.class, carritoDeComprasId);
    }

    public Agendamiento updateOne(Agendamiento agendamiento) {
        final String agendamientoId = agendamiento.getAgendamientoId();
        return restTemplate.postForObject("http://agendamiento-app.railway.internal:8083/v1/agendamientos/{agendamientoId}/modificar", agendamiento, Agendamiento.class, agendamientoId);
    }

    public String cancelOneById(String agendamientoId) {

        return restTemplate.getForObject("http://agendamiento-app.railway.internal:8083/v1/agendamientos/{agendamientoId}/cancelar", String.class, agendamientoId);
    }

    public Agendamiento cancelOnePaidById(String agendamientoId) {
        return restTemplate.getForObject("http://agendamiento-app.railway.internal:8083/v1/agendamientos/{agendamientoId}/cancelar/pagos", Agendamiento.class, agendamientoId);
    }

    public List<Agendamiento> listByUsuarioClienteId(Short usuarioClienteId) {
        return restTemplate.getForObject("http://agendamiento-app.railway.internal:8083/v1/filtrar-por-cliente/{usuarioClienteId}", List.class, usuarioClienteId);
    }

    /*public void reagendarServicio(String agendamientoId, Agendamiento agendamiento) {
        agendamiento.setAgendamientoId(agendamientoId);

        producer.send("Agendamientos-Queue", "Reagendar Servico" +
                "io");

        log.info("Mensaje '{}' enviado con éxito a AGENDAMIENTO-APP en espera de respuesta");
    }

     */
}
