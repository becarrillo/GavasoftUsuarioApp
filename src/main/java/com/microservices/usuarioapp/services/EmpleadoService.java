package com.microservices.usuarioapp.services;

import com.microservices.usuarioapp.entities.Empleado;
import com.microservices.usuarioapp.entities.Usuario;
import com.microservices.usuarioapp.exceptions.ResourceNotFoundException;
import com.microservices.usuarioapp.models.UsuarioRol;
import com.microservices.usuarioapp.repositories.IEmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Service
public class EmpleadoService {

    @Autowired
    private IEmpleadoRepository iEmpleadoRepository;

    public Empleado save(Empleado empleado) throws SQLException {

        return iEmpleadoRepository.save(empleado);
    }

    public Short assignRol(Short usuarioId, String rol) {

        return (short) iEmpleadoRepository.setEmpleadoRol(usuarioId, rol);
    }

    public List<Empleado> getAllEmpleados() {
        return iEmpleadoRepository.listAll();
    }

    public Empleado getEmpleado(String numDocumento) {
        return Optional.of(iEmpleadoRepository.findOne(numDocumento))
            .orElseThrow(ResourceNotFoundException::new);
    }

    public Empleado getEmpleadoByUsuarioId(Short usuarioId) {
        return Optional.of(iEmpleadoRepository.findOneByUsuarioId(usuarioId))
            .orElseThrow(() -> new ResourceNotFoundException(
                "No existe el empleado, o no tiene un rol de empleado con ese id de usuario"
                )
            )
        ;
    }

    public List<Empleado> getEmpleadosByNombre(String nombre) {
        return Optional.of(iEmpleadoRepository.listByNombre(nombre))
            .orElseThrow(ResourceNotFoundException::new);
    }

    public Empleado getEmpleadoByEmail(String email) {
        return iEmpleadoRepository.findOneByEmail(email);
    }

    public Usuario getEmpleadoUsuarioByEmail(String email) {
        return iEmpleadoRepository.getEmpleadoUsuarioByEmail(email);
    }

    public String getEmpleadoRolByUsuarioId(Short usuarioEmpleadoId) {
        return iEmpleadoRepository.getEmpleadoRolByUsuarioId(usuarioEmpleadoId);
    }

    public Short getUsuarioEmpleadoId(String empleadoNumDocumento) {
        return Optional.of(iEmpleadoRepository.getUsuarioIdByNumDocumento(empleadoNumDocumento))
            .orElseThrow(ResourceNotFoundException::new);
    }

    public List<Empleado> listEmpleadosWithRolAsNull() {
        return iEmpleadoRepository.listOnlyEmpleadosWithRolAsNull();
    }

    public Short updateEmpleadoByUsuarioId(Short usuarioEmpleadoId, Empleado empleado) {
        return iEmpleadoRepository.updateByUsuarioId(usuarioEmpleadoId, empleado);
    }

    public Short deleteByUsuarioId(Short usuarioEmpleadoId) {
        return iEmpleadoRepository.deleteByUsuarioId(usuarioEmpleadoId);
    }
}
