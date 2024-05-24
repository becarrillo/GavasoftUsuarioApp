package com.microservices.usuarioapp.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UsuarioDto {
    String email;
    String password;
    String rol;
}
