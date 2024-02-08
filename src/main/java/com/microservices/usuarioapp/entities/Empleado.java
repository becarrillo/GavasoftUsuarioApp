package com.microservices.usuarioapp.entities;

import lombok.Getter;
import lombok.Setter;
import java.sql.Date;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Getter
@Setter
public class Empleado extends Usuario {
    private Short usuario_empleado_id;
    private String apellidos;
    private String tel;
    private String nombre;
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
            Date fecha_entrada,
            Date fecha_retiro
    ) {
        super(apellidos, nombre, email, password, rol, tel);
        this.apellidos = super.getApellidos();
        this.nombre = super.getNombre();
        this.tel = super.getTel();
        this.tipo_documento = tipo_documento;
        this.num_documento = num_documento;
        this.url_fotografia = url_fotografia;
        this.fecha_entrada = fecha_entrada;
        this.fecha_retiro = fecha_retiro;
    }

    public Empleado() {
        super();
    }
}
