package com.microservices.usuarioapp.external.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Factura {
    @Setter private int facturaId;
    private String clienteNumDocumento;
    @Setter private Short iva;
    @Setter private int total;
    @Setter private String conceptoDePago;
    @Setter private byte estadoDePago;
    @Setter private Date fechaDePago;
    private String metodoDePago;
    private String carritoDeComprasId;
    @Setter private LocalDateTime fechaHora;

    public Factura(
            String clienteNumDocumento,
            String conceptoDePago,
            String metodoDePago,
            String carritoDeComprasId
    ) {
        this.clienteNumDocumento = clienteNumDocumento;
        this.conceptoDePago = conceptoDePago;
        this.estadoDePago = 0;
        this.metodoDePago = metodoDePago;
        this.carritoDeComprasId = carritoDeComprasId;
    }

    public Factura(
            int facturaId,
            String clienteNumDocumento,
            String conceptoDePago,
            String metodoDePago,
            String carritoDeComprasId
    ) {
        this.facturaId = facturaId;
        this.clienteNumDocumento = clienteNumDocumento;
        this.conceptoDePago = conceptoDePago;
        this.estadoDePago = 0;
        this.metodoDePago = metodoDePago;
        this.carritoDeComprasId = carritoDeComprasId;
    }
}
