package com.microservices.usuarioapp.services;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.repositories.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

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

    public Cliente getOne(String numDocumento) {
        final Optional<Cliente> opt = Optional.ofNullable(iClienteRepository.findOne(numDocumento));

        if (!opt.isPresent()) {
            return null;
        }
        return opt.get();
    }

    public Short getUsuarioClienteId(String numDocumento) {
        return iClienteRepository.getUsuarioId(numDocumento);
    }

    public Collection<Cliente> getAllClientes() {
        return iClienteRepository.listAll();
    }

    public Short getUsuarioClienteIdByNumDocumento(String numDocumento) {
        return iClienteRepository.findUsuarioClienteIdByNumDocumento(numDocumento);
    }

    public Short updateByUsuario(String numDocumento, Cliente cliente) {
        return iClienteRepository.updateByUsuario(numDocumento, cliente);
    }
}
