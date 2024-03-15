package com.microservices.usuarioapp.controllers;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.entities.Empleado;
import com.microservices.usuarioapp.exceptions.ResourceNotFoundException;
import com.microservices.usuarioapp.external.models.Agendamiento;
import com.microservices.usuarioapp.external.models.Servicio;
import com.microservices.usuarioapp.external.services.AgendamientoService;
import com.microservices.usuarioapp.external.services.CarritoService;
import com.microservices.usuarioapp.external.services.ServicioService;
import com.microservices.usuarioapp.models.UsuarioRol;
import com.microservices.usuarioapp.services.ClienteService;
import com.microservices.usuarioapp.services.EmpleadoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@CrossOrigin("*")
@RequestMapping(path = "/usuarios/empleados")
@Slf4j
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private AgendamientoService agendamientoService;

    @Autowired
    private CarritoService carritoService;

    @PostMapping(path = "/menu-administrador/admin-empleados/crear-cuenta/nuevo")
    public ResponseEntity<String> crearCuentaEmpleado(@RequestBody() Empleado empleado) throws SQLException {
        return new ResponseEntity<String>(
                "Se insertaron "+empleadoService.saveEmpleado(empleado)+" registro(s) con éxito de empleado(s).",
                HttpStatus.CREATED
        );
    }

    @PostMapping(path = "/menu-asesor/solicitudes/clientes/crear-cuenta")
    public ResponseEntity<String> crearCuentaCliente(
            @RequestBody() Cliente cliente
    ) throws SQLException {
        return new ResponseEntity<String>(clienteService.save(cliente)+" registro(s) con éxito de cliente(s)", HttpStatus.CREATED);
    }

    @GetMapping(path = "/menu-asesor/solicitudes/clientes/{numDocumento}")
    public ResponseEntity<Cliente> consultarCliente(@PathVariable("numDocumento") String clienteNumDocumento) {
        final Cliente cliente = clienteService.getOne(clienteNumDocumento);

        if (cliente == null) {
            throw new ResourceNotFoundException("El cliente no existe");
        }
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @PutMapping(path = "/menu-administrador/admin-empleados/empleados/{usuarioEmpleadoId}/asignar-rol")
    public ResponseEntity<Short> asignarRol(
            @PathVariable("usuarioEmpleadoId") Short usuarioEmpleadoId,
            @RequestBody UsuarioRol usuarioRol
    ) {
        usuarioRol.setUsuarioId(usuarioEmpleadoId);
        return new ResponseEntity<Short>(
                empleadoService.assignRol(usuarioRol.getUsuarioId(), usuarioRol.getRol()),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<List<Empleado>> listarEmpleados() {
        return new ResponseEntity<List<Empleado>>(empleadoService.getAllEmpleados(), HttpStatus.OK);
    }

    @GetMapping(path = "/{usuarioId}")
    public ResponseEntity<Empleado> getEmpleado(@PathVariable("usuarioId") Short id) {
        final Empleado empleado = empleadoService.getEmpleado(id);
        return new ResponseEntity<Empleado>(empleado, HttpStatus.FOUND);
    }

    @GetMapping(path = "/menu-administrador/agendamientos")
    public ResponseEntity<List<Agendamiento>> listarAgendamientos() {
        return new ResponseEntity<>(this.agendamientoService.listAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/menu-asesor/agendamientos/clientes/{numDocumento}")
    public List<Agendamiento> listarAgendamientosByCliente(@PathVariable("numDocumento") String numDocumento) {
        return this.agendamientoService.listByClienteNumDocumento(numDocumento);
    }

    @PostMapping(path = "/menu-administrador/servicios/agregar/nuevo")
    public ResponseEntity<String> agregarServicio(@RequestBody() Servicio servicio) throws IOException {
        final Servicio myServicio = servicioService.save(servicio);
        if (myServicio == null) {
            throw new RuntimeException("El cuerpo de la petición contiene campos de datos con restricción definida por duplicidad, no se guardó el servicio");
        }
        return new ResponseEntity<String>(myServicio.toString(), HttpStatus.CREATED);
    }

    @GetMapping(path = "/menu-administrador/servicios/consultar/{servicioNombre}")
    public ResponseEntity<Servicio> consultarServicio(@PathVariable("servicioNombre") String name) throws IOException {
        final Servicio servicio = servicioService.getOne(name);

        if (servicio != null) {
            return new ResponseEntity<Servicio>(servicio, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("El Servicio consultado no existe");
    }

    @PostMapping(path = "/menu-administrador/servicios/modificar/{name}")
    public Servicio modificarServicio(@PathVariable("name") String servicioNombre, @RequestBody Servicio servicio) throws IOException {
        return servicioService.updateOne(servicioNombre, servicio);
    }

    @DeleteMapping(path = "/menu-administrador/servicios/eliminar/{id}")
    public ResponseEntity<String> eliminarServicio(@PathVariable("id") String servicioId) {
        final String delServiceId = servicioService.deleteOneById(servicioId);
        if (delServiceId != null) {
            return new ResponseEntity<>(delServiceId, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("El Servicio con el id no existe, no se eliminará registro alguno");
    }

    @GetMapping(path = "/listar-servicios")
    public ResponseEntity<List<Servicio>> listarServicios() {
        return new ResponseEntity<>(servicioService.getAll(), HttpStatus.OK);
    }

    @PostMapping(path = "/menu-asesor/solicitudes/clientes/{numDocumento}/agendar-servicio")
    public ResponseEntity<Agendamiento> agendarServicio(
            @PathVariable String numDocumento,
            @RequestBody Agendamiento agendamiento
    ) {
        if (!agendamiento.getUsuarioClienteId().equals(clienteService.getUsuarioClienteIdByNumDocumento(numDocumento))) {
            throw new NoSuchElementException("El id de usuario del cliente consultado no coincide con el del agendamiento");
        }
        // Verifica asociación de un carrito de compras con este agendamiento por el campo carritoId, nulo no cumple
        if (agendamiento.getCarritoDeComprasId() == null) {
            agendamiento.setCarritoDeComprasId(carritoService.create());        // Id de carrito de compras asociado
        }

        final Agendamiento newAgendamiento;
        
        try {
            newAgendamiento = agendamientoService.save(agendamiento);
            if (newAgendamiento == null) {
                throw new ResourceNotFoundException("El cliente con el id de usuario del cuerpo de petición no existe");
            } else {
                log.info(
                        "Carrito de Compras del agendamiento tomado: '{}'",
                        carritoService.addSubtotal(newAgendamiento.getCarritoDeComprasId(), newAgendamiento.getServicioId())
                );
            }
            return new ResponseEntity<Agendamiento>(newAgendamiento, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping(path = "/menu-asesor/solicitudes/clientes/{numDocumento}/agendamientos")
    public ResponseEntity<Map<String, Object>> obtenerClienteAgendamientos(@PathVariable("numDocumento") String clienteNumDocumento) {
        final Map<String, Object> map = new HashMap<>();

        map.put("cliente", clienteService.getCliente(clienteNumDocumento));
        map.put("agendamientos", agendamientoService.listByClienteNumDocumento(clienteNumDocumento));
        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    @PostMapping(
            path = "/menu-asesor/solicitudes/clientes/{numDocumento}/reagendar-servicios/agendamientos/{agendamientoId}"
    )
    public ResponseEntity<Agendamiento> reagendarServicio(
            @PathVariable("numDocumento") String clienteNumDocumento,
            @PathVariable("agendamientoId") String agendamientoId,
            @RequestBody Agendamiento agendamiento
    ) {
        agendamiento.setAgendamientoId(agendamientoId);
        Agendamiento newAgendamiento;
        try {
            if (
                    agendamiento.getUsuarioClienteId().equals(clienteService.getUsuarioClienteIdByNumDocumento(clienteNumDocumento)) &&
                            agendamiento.getEstado().equals("pago")
            ) {
                newAgendamiento = agendamientoService.updateOne(agendamiento);
                return new ResponseEntity<Agendamiento>(newAgendamiento, HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatusCode.valueOf(500));
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping(path = "/menu-asesor/solicitudes/clientes/{clienteNumDocumento}/cancelar-servicios/agendamientos/{agendamientoId}")
    public ResponseEntity<String> cancelarServicioAgendado(
            @PathVariable("clienteNumDocumento") String usuarioClienteNumDocumento,
            @PathVariable("agendamientoId") String agendamientoId
    ) {
        final String delAgendamientoId;
        if (!agendamientoService.listByUsuarioClienteId(clienteService.getUsuarioClienteIdByNumDocumento(usuarioClienteNumDocumento)).isEmpty()) {
            delAgendamientoId = agendamientoService.cancelOneById(agendamientoId);
            if (delAgendamientoId != null) {
                return new ResponseEntity<String>(delAgendamientoId, HttpStatus.OK);
            } else {
                throw new ResourceNotFoundException(
                        "El agendamiento con id del cuerpo de petición para cancelar el servicio no se eliminó porque no existe"
                );
            }
        } else {
            throw new RuntimeException();
        }
    }
}
