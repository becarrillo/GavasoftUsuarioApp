package com.microservices.usuarioapp.external.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    private String carritoId;
    private int subtotal;

    public Carrito(int subtotal) {
        this.subtotal = subtotal;
    }
}
