package com.microservices.usuarioapp.controllers;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.entities.Empleado;
import com.microservices.usuarioapp.exceptions.ResourceNotFoundException;
import com.microservices.usuarioapp.external.models.Agendamiento;
import com.microservices.usuarioapp.external.models.Servicio;
import com.microservices.usuarioapp.external.services.AgendamientoService;
import com.microservices.usuarioapp.external.services.ServicioService;
import com.microservices.usuarioapp.services.ClienteService;
import com.microservices.usuarioapp.services.EmpleadoService;
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

@RestController
@CrossOrigin("*")
@RequestMapping(path = "/usuarios")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private AgendamientoService agendamientoService;

    @PostMapping(path = "/empleados/menu-administrador/admin-empleados/crear-cuenta/nuevo")
    public ResponseEntity<String> crearCuentaEmpleado(@RequestBody() Empleado empleado) throws SQLException {
        return new ResponseEntity<String>(
                "Se insertaron "+empleadoService.saveEmpleado(empleado)+" registro(s) con éxito de empleado(s).",
                HttpStatus.CREATED
        );
    }

    @PostMapping(path = "/empleados/menu-asesor/solicitudes/clientes/crear-cuenta")
    public ResponseEntity<String> crearCuentaCliente(
            @RequestBody() Cliente cliente
    ) throws SQLException {
        return new ResponseEntity<String>(clienteService.save(cliente)+" registro(s) con éxito de cliente(s)", HttpStatus.CREATED);
    }

    @GetMapping(path = "empleados/menu-asesor/solicitudes/clientes/{numDocumento}")
    public ResponseEntity<Cliente> consultarCliente(@PathVariable("numDocumento") String clienteNumDocumento) {
        final Cliente cliente = clienteService.getOne(clienteNumDocumento);

        if (cliente == null) {
            throw new ResourceNotFoundException("El cliente no existe");
        }
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @PatchMapping(path = "empleados/menu-administrador/admin-empleados/asignar-rol/{usuarioEmpleadoId}")
    public ResponseEntity<Short> asignarRol(
            @PathVariable("usuarioEmpleadoId") Short usuarioEmpleadoId,
            @RequestBody() String rol
    ) {
        return new ResponseEntity<Short>(empleadoService.assignRol(usuarioEmpleadoId, rol), HttpStatus.OK);
    }

    @GetMapping(path = "/empleados")
    public ResponseEntity<List<Empleado>> listarEmpleados() {
        return new ResponseEntity<List<Empleado>>(empleadoService.getAllEmpleados(), HttpStatus.OK);
    }

    @GetMapping(path = "/empleados/{usuarioId}")
    public ResponseEntity<Empleado> getEmpleado(@PathVariable("usuarioId") Short id) {
        final Empleado empleado = empleadoService.getEmpleado(id);
        return new ResponseEntity<Empleado>(empleado, HttpStatus.FOUND);
    }

    @GetMapping(path = "/empleados/menu-administrador/agendamientos")
    public ResponseEntity<List<Agendamiento>> listarAgendamientos() {
        return new ResponseEntity<>(this.agendamientoService.listAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/empleados/menu-asesor/agendamientos/clientes/{numDocumento}")
    public List<Agendamiento> listarAgendamientosByCliente(@PathVariable("numDocumento") String numDocumento) {
        return this.agendamientoService.listByClienteNumDocumento(numDocumento);
    }

    @PostMapping(path = "/empleados/menu-administrador/servicios/agregar/nuevo")
    public ResponseEntity<String> agregarServicio(@RequestBody() Servicio servicio) throws IOException {
        final Servicio myServicio = servicioService.save(servicio);
        if (myServicio == null) {
            throw new RuntimeException("El cuerpo de la petición contiene campos de datos con restricción definida por duplicidad, no se guardó el servicio");
        }
        return new ResponseEntity<String>(myServicio.toString(), HttpStatus.CREATED);
    }

    @GetMapping(path = "/empleados/menu-administrador/servicios/consultar/{servicioNombre}")
    public ResponseEntity<Servicio> consultarServicio(@PathVariable("servicioNombre") String name) throws IOException {
        final Servicio servicio = servicioService.getOne(name);

        if (servicio != null) {
            return new ResponseEntity<Servicio>(servicio, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("El Servicio consultado no existe");
    }

    @PostMapping(path = "/empleados/menu-administrador/servicios/modificar/{name}")
    public Servicio modificarServicio(@PathVariable("name") String servicioNombre, @RequestBody Servicio servicio) throws IOException {
        return servicioService.updateOne(servicioNombre, servicio);
    }

    @DeleteMapping(path = "/empleados/menu-administrador/servicios/eliminar/{id}")
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

    @PostMapping(path = "/empleados/menu-asesor/solicitudes/agendamientos/agendar/nuevo-servicio")
    public ResponseEntity<Agendamiento> agregarAgendamiento(@RequestBody Agendamiento agendamiento) {
        final Agendamiento newAgendamiento;
        
        try {
            newAgendamiento = agendamientoService.save(agendamiento);
            if (newAgendamiento == null) {
                throw new ResourceNotFoundException("El cliente con el id de usuario del cuerpo de petición no existe");
            }
            return new ResponseEntity<Agendamiento>(newAgendamiento, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping(path = "/empleados/menu-asesor/solicitudes/clientes/{numDocumento}")
    public ResponseEntity<Map<String, Object>> obtenerClienteAgendamientos(@PathVariable("numDocumento") String clienteNumDocumento) {
        final Map<String, Object> map = new HashMap<>();

        map.put("cliente", clienteService.getCliente(clienteNumDocumento));
        map.put("agendamientos", agendamientoService.listByClienteNumDocumento(clienteNumDocumento));
        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    @PostMapping(
            path = "/empleados/menu-asesor/solicitudes/clientes/{numDocumento}/reagendar-servicios/agendamientos/{agendamientoId}"
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

    @DeleteMapping(path = "/empleados/menu-asesor/solicitudes/clientes/{clienteNumDocumento}/cancelar-servicios/agendamientos/{agendamientoId}")
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
