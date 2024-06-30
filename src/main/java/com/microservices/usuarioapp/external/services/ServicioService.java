package com.microservices.usuarioapp.external.services;

import com.microservices.usuarioapp.external.models.Servicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class ServicioService {
    @Autowired
    private RestTemplate restTemplate;
    /*
        SERVICIO-APP presente en la url de las peticiones a través de RestTemplate,
        así se registró el microservicio agendamientos en servidor Eureka y éste lo localiza
        para la comunicación desde el presente proyecto
    */
    public Servicio save(Servicio servicio) {
        return restTemplate.postForObject("http://SERVICIO-APP/v1/servicios/agregar/nuevo", servicio, Servicio.class);
    }

    public Servicio getOne(String servicioName) {
        return restTemplate.getForObject("http://SERVICIO-APP/v1/servicios/consultar-por-nombre/"+servicioName, Servicio.class);
    }

    public Servicio getOneById(String servicioId){
        return restTemplate.getForObject("http://SERVICIO-APP/v1/servicios/"+servicioId, Servicio.class);
    }

    @SuppressWarnings("unchecked")
    public List<Servicio> getAll() {
        return restTemplate.getForObject("http://SERVICIO-APP/v1/servicios", List.class);
    }

    public Servicio updateOne(String servicioNombre, com.microservices.usuarioapp.external.models.Servicio servicio) {
        final String name = servicioNombre;
        return restTemplate.postForObject(
                "http://SERVICIO-APP/v1/servicios/{name}/modificar",
                servicio,
                Servicio.class,
                name
        );
    }

    public String deleteOneById(String servicioId) {
        final String id = servicioId;
        return restTemplate.getForObject("http://SERVICIO-APP/v1/servicios/{id}/eliminar", String.class, id);
    }

}
