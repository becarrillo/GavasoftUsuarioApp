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

    public String getClienteNumDocumentoByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.getClienteNumDocumentoByUsuarioId(usuarioClienteId);
    }

    public int getClienteSaldoFavor(String clienteNumDocumento) {
        return iClienteRepository.getClienteSaldoFavor(clienteNumDocumento);
    }

    public String getClienteTipoDocumentoByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.getClienteTipoDocumentoByUsuarioId(usuarioClienteId);
    }

    public String getClienteNombreByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.getClienteNombreByUsuarioId(usuarioClienteId);
    }

    public String getClienteApellidosByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.getClienteApellidosByUsuarioId(usuarioClienteId);
    }

    public Short getUsuarioClienteIdByNumDocumento(String numDocumento) {
        return iClienteRepository.getUsuarioId(numDocumento);
    }

    public Cliente getClienteByUsuarioId(Short usuarioId) {
        return iClienteRepository.findOneByUsuarioId(usuarioId);
    }

    public List<Cliente> listAllClientes() {
        return iClienteRepository.listAll();
    }

    public Cliente getCliente(String numDocumento) {
        return iClienteRepository.findOne(numDocumento);
    }

    public Cliente updateByUsuarioId(Short usuarioClienteId, Cliente cliente) {
        return iClienteRepository.updateByUsuarioId(usuarioClienteId, cliente);
    }

    public short deleteByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.deleteByUsuarioId(usuarioClienteId);
    }
}
