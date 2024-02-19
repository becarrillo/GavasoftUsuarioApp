package com.microservices.usuarioapp.repositories;

import com.microservices.usuarioapp.entities.Cliente;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IClienteRepository {
    int save(Cliente cliente);
    short getUsuarioId(String numDocumento);
    Cliente findOne(String numDocumento);
    Cliente findOneByUsuarioId(Short usuarioId);
    Cliente findUsuarioClienteByNumDocumento(String numDocumento);
    List<Cliente> listAll();
    short updateByUsuario(String numDocumento, Cliente cliente);
    short deleteByUsuarioId(Short usuarioId);
}
