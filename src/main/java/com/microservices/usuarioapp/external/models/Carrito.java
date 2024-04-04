package com.microservices.usuarioapp.external.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Carrito {
    private String carritoId;
    private int subtotal;
    private List<Agendamiento> agendamientosList;

    public Carrito(String carritoId, int subtotal) {
        this.carritoId = carritoId;
        this.subtotal = subtotal;
    }
}
