package com.microservices.usuarioapp.services;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.entities.Empleado;
import com.microservices.usuarioapp.repositories.IClienteRepository;
import com.microservices.usuarioapp.repositories.IEmpleadoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Service
@Slf4j
public class EmpleadoService {
    @Autowired
    private IClienteRepository iClienteRepository;

    @Autowired
    private IEmpleadoRepository iEmpleadoRepository;

    public void enviarMsjConRabbitMQ(String message) {
        log.info("El mensaje {} fue enviado con ÉXITO. ", message);
        //producer.send(message);
    }

    public short save(Empleado empleado) throws SQLException {

        return iEmpleadoRepository.save(empleado);
    }

    public Short assignRol(Short usuarioId, String rol) {

        return (short) iEmpleadoRepository.setEmpleadoRol(usuarioId, rol);
    }

    public List<Empleado> getAllEmpleados() {
        return iEmpleadoRepository.listAll();
    }

    public Empleado getEmpleadoByUsuarioId(Short usuarioId) {
        return iEmpleadoRepository.findOneByUsuarioId(usuarioId);
    }

    public String getEmpleadoRolByUsuarioId(Short usuarioEmpleadoId) {
        return iEmpleadoRepository.getEmpleadoRolByUsuarioId(usuarioEmpleadoId);
    }

    public Cliente getClienteByUsuarioId(Short usuarioId) {
        return iClienteRepository.findOneByUsuarioId(usuarioId);
    }

    public Empleado updateEmpleadoByUsuarioId(Short usuarioEmpleadoId, Empleado empleado) {
        return iEmpleadoRepository.updateByUsuarioId(usuarioEmpleadoId, empleado);
    }

    public short deleteByUsuarioId(Short usuarioEmpleadoId) {
        return iEmpleadoRepository.deleteByUsuarioId(usuarioEmpleadoId);
    }
}
