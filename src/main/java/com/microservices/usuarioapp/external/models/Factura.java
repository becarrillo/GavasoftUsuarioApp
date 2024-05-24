package com.microservices.usuarioapp.external.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class Factura {
    @Setter private int facturaId;
    private String clienteNumDocumento;
    @Setter private Short iva;
    @Setter private int total;
    @Setter private byte estadoDePago;
    @Setter private Date fechaDePago;
    private String metodoDePago;
    private String carritoDeComprasId;
    @Setter private LocalDateTime fechaHora;

    public Factura(
            String clienteNumDocumento,
            String metodoDePago,
            String carritoDeComprasId
    ) {
        this.clienteNumDocumento = clienteNumDocumento;
        this.metodoDePago = metodoDePago;
        this.carritoDeComprasId = carritoDeComprasId;
    }

    public Factura(
            int facturaId,
            String clienteTipoDocumento,
            String clienteNumDocumento,
            Short iva,
            int total,
            byte estadoDePago,
            String metodoDePago,
            String carritoDeComprasId
    ) {
        this.facturaId = facturaId;
        this.clienteNumDocumento = clienteNumDocumento;
        this.iva = iva;
        this.total = total;
        this.estadoDePago = estadoDePago;
        this.metodoDePago = metodoDePago;
        this.carritoDeComprasId = carritoDeComprasId;
    }
}
