package com.microservices.usuarioapp.services;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.repositories.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Service
public class ClienteService {
    @Autowired
    private IClienteRepository iClienteRepository;

    public int save(Cliente cliente) {
        return iClienteRepository.save(cliente);
    }
    public Collection<Cliente> getAllClientes() {
        return iClienteRepository.listAll();
    }

    public Short getUsuarioClienteIdByNumDocumento(String numDocumento) {
        return iClienteRepository.findOne(numDocumento);
    }
}
