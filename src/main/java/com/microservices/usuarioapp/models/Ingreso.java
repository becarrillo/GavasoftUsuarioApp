package com.microservices.usuarioapp.models;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ingreso {
    private int facturaId;
    private LocalDateTime fechaHoraDelServicio;
    private String servicioId;
    private String servicioNombre;
    private int servicioPrecio;
    private String clienteTipoDocumento;
    private String clienteNumDocumento;
    private LocalDate fechaDeConsulta;

    public Ingreso(
            int facturaId,
            LocalDateTime fechaHoraDelServicio,
            String servicioId,
            String servicioNombre,
            int servicioPrecio
    ) {
        this.facturaId = facturaId;
        this.fechaHoraDelServicio = fechaHoraDelServicio;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.servicioPrecio = servicioPrecio;
        this.fechaDeConsulta = LocalDate.now(ZoneId.of("GMT-5"));
    }
}
