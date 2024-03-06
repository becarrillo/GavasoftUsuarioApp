package com.microservices.usuarioapp.external.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Time;

@Getter
@Setter
@ToString
public class Servicio {
    private String servicioId;
    private String servicioNombre;
    private String descripcion;
    private Time duracion;
    private Integer precio;
    private String imgUrl;

    public Servicio(
            String servicioNombre,
            String descripcion,
            Time duracion,
            Integer precio,
            String imgUrl
    ) {
        this.servicioNombre = servicioNombre;
        this.descripcion = descripcion;
        this.duracion = duracion;
        this.precio = precio;
        this.imgUrl = imgUrl;
    }
}
