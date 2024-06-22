package com.microservices.usuarioapp.repositories;

import com.microservices.usuarioapp.entities.Empleado;
import com.microservices.usuarioapp.entities.Usuario;

import org.springframework.stereotype.Repository;
import java.sql.SQLException;
import java.util.List;

@Repository
public interface IEmpleadoRepository {
    Empleado save(Empleado empleado) throws SQLException;
    List<Empleado> listAll();
    List<Empleado> listOnlyEmpleadosWithRolAsNull();
    Empleado findOne(String numDocumento);
    Empleado findOneByUsuarioId(Short usuarioId);
    Empleado findOneByEmail(String email);
    List<Empleado> listByNombre(String nombre);
    Usuario getEmpleadoUsuarioByEmail(String email);
    String getEmpleadoRolByUsuarioId(Short usuarioEmpleadoId);
    Short getUsuarioIdByNumDocumento(String numDocumento);
    short setEmpleadoRol(Short usuarioId, String rol);
    Short updateByUsuarioId(short usuarioId, Empleado empleado);
    Short deleteByUsuarioId(short usuarioId);
}
