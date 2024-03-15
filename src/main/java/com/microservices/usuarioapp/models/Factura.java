package com.microservices.usuarioapp.models;

import com.microservices.usuarioapp.external.models.Agendamiento;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class Factura {
    @Setter private int factura_id;
    private final String clienteTipoDocumento;
    private final String clienteNumDocumento;
    private final Short iva;
    private final int total;
    @Setter
    private byte estadoDePago = 0;  // Por defecto es equivalente a un false (NO PAGO)
    private final String metodoDePago;
    private final String carritoDeComprasId;
    @Setter private LocalDateTime fechaHora;
    @Setter private List<Agendamiento> agendamientosList;

    public Factura(
            String clienteTipoDocumento,
            String clienteNumDocumento,
            Short iva,
            int total,
            String metodoDePago,
            String carritoDeComprasId
    ) {
        this.clienteTipoDocumento = clienteTipoDocumento;
        this.clienteNumDocumento = clienteNumDocumento;
        this.iva = iva;
        this.total = total;
        this.metodoDePago = metodoDePago;
        this.carritoDeComprasId = carritoDeComprasId;
    }
}
