package com.microservices.usuarioapp.services;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.repositories.IClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
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

        return opt.orElse(null);
    }

    public Short getUsuarioClienteIdByNumDocumento(String numDocumento) {
        return iClienteRepository.getUsuarioId(numDocumento);
    }

    public List<Cliente> getAllClientes() {
        return iClienteRepository.listAll();
    }

    public Cliente getCliente(String numDocumento) {
        return iClienteRepository.findUsuarioClienteByNumDocumento(numDocumento);
    }

    public Short updateByUsuario(String numDocumento, Cliente cliente) {
        return iClienteRepository.updateByUsuario(numDocumento, cliente);
    }
}
