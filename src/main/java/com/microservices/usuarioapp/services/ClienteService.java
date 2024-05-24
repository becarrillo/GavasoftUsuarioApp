package com.microservices.usuarioapp.services;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.repositories.IClienteRepository;
import jakarta.annotation.security.RolesAllowed;
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

    @RolesAllowed({"USUARIO", "asesor"})
    public int save(Cliente cliente) {
        return iClienteRepository.save(cliente);
    }

    @RolesAllowed({"administrador", "asesor"})
    public Cliente getOne(String numDocumento) {
        final Optional<Cliente> opt = Optional.ofNullable(iClienteRepository.findOne(numDocumento));

        return opt.orElse(null);
    }

    @RolesAllowed({"administrador", "asesor", "cliente"})
    public String getClienteNumDocumentoByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.getClienteNumDocumentoByUsuarioId(usuarioClienteId);
    }

    @RolesAllowed({"administrador", "asesor", "cliente"})
    public String getClienteTipoDocumentoByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.getClienteTipoDocumentoByUsuarioId(usuarioClienteId);
    }

    @RolesAllowed({"administrador", "asesor", "cliente"})
    public String getClienteNombreByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.getClienteNombreByUsuarioId(usuarioClienteId);
    }

    public String getClienteApellidosByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.getClienteApellidosByUsuarioId(usuarioClienteId);
    }

    @RolesAllowed({"administrador", "asesor"})
    public Short getUsuarioClienteIdByNumDocumento(String numDocumento) {
        return iClienteRepository.getUsuarioId(numDocumento);
    }


    public Cliente getClienteByUsuarioId(Short usuarioId) {
        return iClienteRepository.findOneByUsuarioId(usuarioId);
    }

    @RolesAllowed({"administrador", "asesor"})
    public List<Cliente> getAllClientes() {
        return iClienteRepository.listAll();
    }

    @RolesAllowed({"administrador", "asesor", "cliente"})
    public Cliente getCliente(String numDocumento) {
        return iClienteRepository.findOne(numDocumento);
    }

    @RolesAllowed({"administrador", "asesor"})
    public Cliente updateByUsuarioId(Short usuarioClienteId, Cliente cliente) {
        return iClienteRepository.updateByUsuarioId(usuarioClienteId, cliente);
    }

    public short deleteByUsuarioId(Short usuarioClienteId) {
        return iClienteRepository.deleteByUsuarioId(usuarioClienteId);
    }
}
