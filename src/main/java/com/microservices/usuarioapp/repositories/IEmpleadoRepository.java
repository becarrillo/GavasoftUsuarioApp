package com.microservices.usuarioapp.repositories;

import com.microservices.usuarioapp.entities.Empleado;
import org.springframework.stereotype.Repository;
import java.sql.SQLException;
import java.util.List;

@Repository
public interface IEmpleadoRepository {
    Short save(Empleado empleado) throws SQLException;
    int setEmpleadoRol(Short usuarioId, String rol);
    List<Empleado> listAll();
    Empleado findOneByUsuarioId(Short usuarioId);

    String getEmpleadoRolByUsuarioId(Short usuarioEmpleadoId);

    Empleado updateByUsuarioId(short usuarioId, Empleado empleado);
    short deleteByUsuarioId(short usuarioId);
}
