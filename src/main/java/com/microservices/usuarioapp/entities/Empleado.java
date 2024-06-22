package com.microservices.usuarioapp.entities;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Getter
@Setter
@Table(name = "empleados")
public class Empleado extends Usuario {
    private String tipo_documento;
    private String num_documento;
    private String url_fotografia;
    private Date fecha_entrada;
    private Date fecha_retiro;

    public Empleado(
            String apellidos,
            String nombre,
            String email,
            String password,
            String rol,
            String tel,
            String tipo_documento,
            String num_documento,
            String url_fotografia,
            Date fecha_entrada
    ) {
        super(apellidos, nombre, email, password, rol, tel);
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.url_fotografia = url_fotografia;
        this.fecha_entrada = fecha_entrada;
    }

    public Empleado(
            String apellidos,
            String nombre,
            String email,
            String password,
            String tel,
            String tipo_documento,
            String num_documento,
            String url_fotografia,
            Date fecha_entrada
    ) {
        super(apellidos, nombre, email, password, null, tel);
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.url_fotografia = url_fotografia;
        this.fecha_entrada = fecha_entrada;
    }

    public Empleado() {
        super();
    }
}
