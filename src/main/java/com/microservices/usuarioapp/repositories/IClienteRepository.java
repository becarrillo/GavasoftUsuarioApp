package com.microservices.usuarioapp.repositories;

import com.microservices.usuarioapp.entities.Cliente;
import java.util.List;

public interface IClienteRepository {
    byte save(Cliente cliente);
    short getUsuarioId(String numDocumento);
    Cliente findOne(String numDocumento);
    Cliente findOneByEmail(String email);
    Cliente findOneByUsuarioId(Short usuarioId);
    Short findUsuarioClienteIdByNumDocumento(String numDocumento);
    String getClienteNumDocumentoByUsuarioId(Short usuarioClienteId);
    Integer getClienteSaldoFavor(String clienteNumDocumento);
    String getClienteTipoDocumentoByUsuarioId(Short usuarioClienteId);
    String getClienteNombreByUsuarioId(Short usuarioClienteId);
    String getClienteApellidosByUsuarioId(Short usuarioClienteId);
    List<Cliente> listAll();
    Cliente updateByUsuarioId(Short usuarioClienteId, Cliente cliente);
    short deleteByUsuarioId(Short usuarioId);
}
