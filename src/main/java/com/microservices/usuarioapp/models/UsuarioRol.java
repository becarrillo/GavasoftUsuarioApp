package com.microservices.usuarioapp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class UsuarioRol {
    @Setter
    private Short usuarioId;

    private final String rol;

    public UsuarioRol(String rol) {
        this.rol = rol;
    }
}
