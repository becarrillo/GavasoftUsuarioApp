package com.microservices.usuarioapp.external.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ServicioValoracion {
    private int valoracionId;
    private byte puntuacion;
    private String opinion;
    private LocalDateTime fechaHora;
    private String servicioId;
}
