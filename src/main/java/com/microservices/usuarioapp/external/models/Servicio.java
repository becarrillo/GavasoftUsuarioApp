package com.microservices.usuarioapp.external.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Servicio {
    private String servicioId;
    private String servicioNombre;
    private String descripcion;
    private short duracion;
    private int precio;
    private String imgUrl;

    public Servicio(
            String servicioNombre,
            String descripcion,
            short duracion,
            int precio,
            String imgUrl
    ) {
        this.servicioNombre = servicioNombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.precio = precio;
        this.imgUrl = imgUrl;
    }
}
