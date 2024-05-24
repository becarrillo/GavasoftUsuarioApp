package com.microservices.usuarioapp.external.models;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
            String servicioId,
            LocalDate fechaDeConsulta
    ) {
        this.facturaId = facturaId;
        this.fechaHoraDelServicio = fechaHoraDelServicio;
        this.servicioId = servicioId;
        this.fechaDeConsulta = fechaDeConsulta;
    }

    public Ingreso(
            int facturaId,
            LocalDateTime fechaHoraDelServicio,
            String servicioId,
            String servicioNombre,
            int servicioPrecio,
            String servicioImgUrl,
            LocalDate fechaDeConsulta
    ) {
        this.facturaId = facturaId;
        this.fechaHoraDelServicio = fechaHoraDelServicio;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.servicioPrecio = servicioPrecio;
        this.servicioImgUrl = servicioImgUrl;
        this.fechaDeConsulta = fechaDeConsulta;
    }

    public Ingreso(
            int facturaId,
            LocalDateTime fechaHoraDelServicio,
            String servicioId,
            String servicioNombre,
            int servicioPrecio,
            String servicioImgUrl,
            String clienteNumDocumento,
            LocalDate fechaDeConsulta
    ) {
        this.facturaId = facturaId;
        this.fechaHoraDelServicio = fechaHoraDelServicio;
        this.servicioId = servicioId;
        this.servicioNombre = servicioNombre;
        this.servicioPrecio = servicioPrecio;
        this.servicioImgUrl = servicioImgUrl;
        this.clienteNumDocumento = clienteNumDocumento;
        this.fechaDeConsulta = fechaDeConsulta;
    }
}
