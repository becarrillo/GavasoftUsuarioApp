package com.microservices.usuarioapp.repositories;

import com.microservices.usuarioapp.entities.Cliente;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IClienteRepository {
    int save(Cliente cliente);
    List<Cliente> listAll();
    Short findOne(String numDocumento);
    Cliente findOneByUsuarioId(Short usuarioId);

    Short findUsuarioClienteIdByNumDocumento(String numDocumento);
    short updateByUsuarioId(String numDocumento, Cliente cliente);
    short deleteByUsuarioId(Short usuarioId);

    byte convertToByteIsAuthorizacionDatos(boolean autorizacionDatos);
}
