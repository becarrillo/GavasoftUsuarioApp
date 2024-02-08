package com.microservices.usuarioapp.entities;

import jdk.jfr.Unsigned;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Getter
@Setter
@NoArgsConstructor
public class Usuario {
    @Unsigned
    private Short usuario_id;
    private String apellidos;
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private String tel;

    public Usuario(
            String apellidos,
            String nombre,
            String email,
            String password,
            String rol,
            String tel
    ) {
        this.apellidos = apellidos;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.tel = tel;
    }
}
