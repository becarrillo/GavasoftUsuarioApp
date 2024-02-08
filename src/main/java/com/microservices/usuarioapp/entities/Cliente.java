package com.microservices.usuarioapp.entities;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Getter
@Setter
public class Cliente extends Usuario {
    private Short usuario_cliente_id;
    private String tipo_documento;
    private String num_documento;
    private boolean autorizacion_datos;
    private int saldo_favor = 0;
    private LocalDateTime fecha_hora_registro;

    public Cliente(
            String apellidos,
            String nombre,
            String email,
            String password,
            String rol,
            String tel,
            String tipo_documento,
            String num_documento,
            boolean autorizacion_datos,
            int saldo_favor
    ) {
        super(apellidos,nombre,email,password,rol,tel);
        this.usuario_cliente_id = super.getUsuario_id();
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.autorizacion_datos = autorizacion_datos;
        this.saldo_favor += saldo_favor;
    }

    public Cliente(
            Short usuario_cliente_id,
            boolean autorizacion_datos,
            int saldo_favor
    ) {
        this.usuario_cliente_id = usuario_cliente_id;
        this.autorizacion_datos = autorizacion_datos;
        this.saldo_favor += saldo_favor;
    }

    public Cliente() {
        super();
    }
}
