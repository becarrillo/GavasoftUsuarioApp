package com.microservices.usuarioapp.entities;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Getter
@Setter
@Table(name = "clientes")
public class Cliente extends Usuario {
    private String tipo_documento;
    private String num_documento;
    private boolean autorizacion_datos;
    private int saldo_favor;
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
            LocalDateTime fecha_hora_registro
    ) {
        super(apellidos,nombre,email,password,rol,tel);  // el rol como null en la BD se guarda 'cliente' por defecto
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.autorizacion_datos = autorizacion_datos;
        this.saldo_favor = 0;
        this.fecha_hora_registro = fecha_hora_registro;
    }

    public Cliente() {
        super();
    }
}
