package com.microservices.usuarioapp.external.models;

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
    private String servicioImgUrl;
    private String clienteTipoDocumento;
    private String clienteNumDocumento;
    private LocalDate fechaDeConsulta;

    public Ingreso(
            int facturaId,
            LocalDateTime fechaHoraDelServicio,
            String servicioId
    ) {
        this.facturaId = facturaId;
        this.fechaHoraDelServicio = fechaHoraDelServicio;
        this.servicioId = servicioId;
        this.fechaDeConsulta = LocalDate.now(ZoneId.of("GMT-5"));
    }

    public Ingreso(
            int facturaId,
            LocalDateTime fechaHoraDelServicio,
            String servicioId,
            String servicioNombre,
            int servicioPrecio,
            String servicioImgUrl
    ) {
        this.facturaId = facturaId;
        this.fechaHoraDelServicio = fechaHoraDelServicio;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.servicioPrecio = servicioPrecio;
        this.servicioImgUrl = servicioImgUrl;
        this.fechaDeConsulta = LocalDate.now(ZoneId.of("GMT-5"));
    }

    public Ingreso(
            int facturaId,
            LocalDateTime fechaHoraDelServicio,
            String servicioId,
            String servicioNombre,
            int servicioPrecio,
            String servicioImgUrl,
            String clienteTipoDocumento,
            String clienteNumDocumento
    ) {
        this.facturaId = facturaId;
        this.fechaHoraDelServicio = fechaHoraDelServicio;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.servicioPrecio = servicioPrecio;
        this.servicioImgUrl = servicioImgUrl;
        this.clienteTipoDocumento = clienteTipoDocumento;
        this.clienteNumDocumento = clienteNumDocumento;
        this.fechaDeConsulta = LocalDate.now(ZoneId.of("GMT-5"));
    }
}
