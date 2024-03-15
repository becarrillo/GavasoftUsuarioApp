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
            String tel,
            String tipo_documento,
            String num_documento,
            boolean autorizacion_datos
    ) {
        super(apellidos,nombre,email,password,"cliente",tel);
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.autorizacion_datos = autorizacion_datos;
    }

    public Cliente() {
        super();
    }
}
