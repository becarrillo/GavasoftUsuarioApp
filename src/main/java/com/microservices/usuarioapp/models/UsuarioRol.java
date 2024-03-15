package com.microservices.usuarioapp.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public class UsuarioRol {
    @Setter
    private Short usuarioId;

    private final String rol;

    public UsuarioRol(String rol) {
        this.rol = rol;
    }
}
