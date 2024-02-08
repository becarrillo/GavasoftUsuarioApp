package com.microservices.usuarioapp.external.models;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class Agendamiento {
    private String agendamientoId;
    private LocalDateTime fechaHora;
    private boolean pago;
    private String servicioId;
    private Short usuarioClienteId;

    public Agendamiento(
            LocalDateTime fechaHora,
            boolean pago,
            String servicioId,
            Short usuarioClienteId
    ) {
        this.fechaHora = fechaHora;
        this.pago = pago;
        this.servicioId = servicioId;
        this.usuarioClienteId = usuarioClienteId;
    }
}