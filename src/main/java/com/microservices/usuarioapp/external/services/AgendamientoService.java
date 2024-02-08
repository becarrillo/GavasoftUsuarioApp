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
        newAgendamiento = restTemplate.postForObject(
            "http://AGENDAMIENTO-APP/agendamientos/agregar/nuevo",
            agendamiento,
            Agendamiento.class
        );
        return newAgendamiento;
    }

    @SuppressWarnings("unchecked")
    public List<Agendamiento> listAll() {
        return restTemplate.getForObject("http://AGENDAMIENTO-APP/agendamientos", List.class);
    }

    /* Busca obtener del proyecto Agendamiento todos aquellos que son pagos de un determinado
       cliente por su número de documento, haciendo un mapeo en los dos gestores de bases de
       datos en cada uno de los microservicios (el de los usuarios y el de los agendamientos)
    */
    @SuppressWarnings("unchecked")
    public List<Agendamiento> listClienteAgendamientos(String numDocumento) {
        final Short usuarioClienteId = iClienteRepository.findUsuarioClienteIdByNumDocumento(numDocumento);

        return restTemplate.getForObject(
                "http://AGENDAMIENTO-APP/agendamientos/clientes/{usuarioClienteId}",
                List.class,
                usuarioClienteId
        );
    }

    public String cancelOneById(String agendamientoId) {
        final String id = agendamientoId;

        return restTemplate.getForObject("http://AGENDAMIENTO-APP/agendamientos/cancelar/{id}", String.class, id);
    }

    /*public void reagendarServicio(String agendamientoId, Agendamiento agendamiento) {
        agendamiento.setAgendamientoId(agendamientoId);

        producer.send("Agendamientos-Queue", "Reagendar Servico" +
                "io");

        log.info("Mensaje '{}' enviado con éxito a AGENDAMIENTO-APP en espera de respuesta");
    }

     */
}
