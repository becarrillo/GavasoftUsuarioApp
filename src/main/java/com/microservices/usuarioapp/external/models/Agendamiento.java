package com.microservices.usuarioapp.external.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Agendamiento {
    private String agendamientoId;
    private LocalDateTime fechaHora;
    private String estado;
    private String servicioId;
    private Short usuarioClienteId;

    public Agendamiento(
            LocalDateTime fechaHora,
            String estado,
            String servicioId,
            Short usuarioClienteId
    ) {
        this.fechaHora = fechaHora;
        this.estado = estado;
        this.servicioId = servicioId;
        this.usuarioClienteId = usuarioClienteId;
    }
}