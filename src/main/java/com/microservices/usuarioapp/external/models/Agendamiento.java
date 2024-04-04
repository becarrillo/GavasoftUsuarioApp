package com.microservices.usuarioapp.external.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Agendamiento {
    private String agendamientoId;
    private LocalDateTime fechaHora;
    private String estado = "tomado";
    private String servicioId;
    private Short usuarioClienteId;
    private String carritoDeComprasId;

    public Agendamiento(
            LocalDateTime fechaHora,
            String servicioId
    ) {
        this.fechaHora = fechaHora;
        this.servicioId = servicioId;
    }

    public Agendamiento(
            LocalDateTime fechaHora,
            String servicioId,
            Short usuarioClienteId
    ) {
        this.fechaHora = fechaHora;
        this.servicioId = servicioId;
        this.usuarioClienteId = usuarioClienteId;
    }
}