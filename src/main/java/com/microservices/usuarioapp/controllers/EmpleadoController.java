package com.microservices.usuarioapp.controllers;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.entities.Empleado;
import com.microservices.usuarioapp.entities.Usuario;
import com.microservices.usuarioapp.exceptions.ResourceNotFoundException;
import com.microservices.usuarioapp.external.models.Agendamiento;
import com.microservices.usuarioapp.external.models.Ingreso;
import com.microservices.usuarioapp.external.models.Servicio;
import com.microservices.usuarioapp.external.services.*;
import com.microservices.usuarioapp.models.UsuarioDto;
import com.microservices.usuarioapp.models.UsuarioRol;
import com.microservices.usuarioapp.services.ClienteService;
import com.microservices.usuarioapp.services.EmpleadoService;

//import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

import org.springframework.http.HttpHeaders;

@RestController
@CrossOrigin(origins = "http://localhost:4200", maxAge = 540L)
@RequestMapping(path = "/v1/usuarios/empleados")
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
    private IngresoService ingresoService;



    @PostMapping(path = "/menu-administrador/admin-empleados/crear-cuenta/nuevo")
    public ResponseEntity<Empleado> crearCuentaEmpleado(@RequestBody() Empleado empleado) throws SQLException {
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Vary", "Origin");
        return new ResponseEntity<Empleado>(
                empleadoService.save(empleado),
                HttpStatus.CREATED);
    }

    @PostMapping(path = "/menu-asesor/solicitudes/clientes/crear-cuenta")
    public ResponseEntity<String> crearCuentaCliente(
            @RequestBody() Cliente cliente) throws SQLException {
        return new ResponseEntity<String>(clienteService.save(cliente) + " registro(s) con éxito de cliente(s)",
                HttpStatus.CREATED);
    }

    @GetMapping(path = "/menu-asesor/solicitudes/clientes/{numDocumento}")
    public ResponseEntity<Cliente> consultarCliente(@PathVariable("numDocumento") String clienteNumDocumento) {
        final Cliente cliente = clienteService.getOne(clienteNumDocumento);

        if (cliente == null) {
            throw new ResourceNotFoundException("El cliente no existe");
        }
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @PostMapping(path = "/menu-administrador/admin-empleados/consultar/{empleadoNumDocumento}/asignar-rol")
    public ResponseEntity<Short> asignarRol(
            @PathVariable String empleadoNumDocumento,
            @RequestBody UsuarioRol usuarioRol
        ) {
        return new ResponseEntity<Short>(
                empleadoService.assignRol(usuarioRol.getUsuarioId(), usuarioRol.getRol()),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/menu-administrador/admin-empleados/consultar/{empleadoNumDocumento}")
    public ResponseEntity<Empleado> consultarEmpleado(@PathVariable String empleadoNumDocumento) {
        final Empleado empleado;
        empleado = empleadoService.getEmpleado(empleadoNumDocumento);
        return new ResponseEntity<Empleado>(empleado, HttpStatus.OK);
    }
    
    @GetMapping(path = "/menu-administrador/admin-empleados/consultar/por-email/{empleadoEmail}")
    public ResponseEntity<Empleado> consultarEmpleadoPorEmail(@PathVariable("empleadoEmail") String email) {
    	final Empleado empleado = empleadoService.getEmpleadoByEmail(email);
    	return new ResponseEntity<Empleado>(empleado, HttpStatus.OK);
    }

    @GetMapping(path = "/menu-administrador/admin-empleados/consultar/login/{email}")
    public ResponseEntity<UsuarioDto> obtenerUsuarioDtoPorEmail(@PathVariable String email) {
        final Usuario usuario = empleadoService.getEmpleadoUsuarioByEmail(email);
        
        return new ResponseEntity<UsuarioDto>(
            new UsuarioDto(usuario.getEmail(), usuario.getPassword(), usuario.getRol()),
            HttpStatus.OK
        );
    }

    @PutMapping(path = "/menu-administrador/admin-empleados/consultar/{empleadoNumDocumento}/modificar")
    public ResponseEntity<Short> modificarCuentaEmpleado(
            @PathVariable String empleadoNumDocumento,
            @RequestBody Empleado empleado) {
        
        final Short rows;
        try {
            rows = empleadoService.updateEmpleadoByUsuarioId(
                empleadoService.getUsuarioEmpleadoId(empleadoNumDocumento),
                empleado
            );
        } catch (Exception ex) {
            throw ex;
        }
        return new ResponseEntity<Short>(rows, HttpStatus.OK);
    }

    @DeleteMapping(path = "/menu-administrador/admin-empleados/consultar/{empleadoNumDocumento}/eliminar")
    public ResponseEntity<Short> eliminarCuentaEmpleado(@PathVariable String empleadoNumDocumento) {
        final Short rows;
        try {
            rows = empleadoService.deleteByUsuarioId(
                    empleadoService.getUsuarioEmpleadoId(empleadoNumDocumento));
        } catch (Exception ex) {
            throw ex;
        }
        return new ResponseEntity<Short>(rows, HttpStatus.OK);
    }

    @GetMapping(path = "/menu-administrador/admin-empleados/listar")
    public ResponseEntity<List<Empleado>> listarEmpleados() {
        return new ResponseEntity<List<Empleado>>(empleadoService.getAllEmpleados(), HttpStatus.OK);
    }

    @GetMapping(path = "/menu-administrador/admin-empleados/consultar/por-nombre/{empleadoNombre}")
    public ResponseEntity<List<Empleado>> listarEmpleadosPorNombre(@PathVariable String empleadoNombre) {
        return new ResponseEntity<List<Empleado>>(
                empleadoService.getEmpleadosByNombre(empleadoNombre),
                HttpStatus.OK);
    }

    @GetMapping(path = "/menu-administrador/admin-empleados/listar/sin-rol")
    public ResponseEntity<List<Empleado>> listarPorRolNull() {
        List<Empleado> empleadosWithRolAsNull = new ArrayList<Empleado>();
        List<UsuarioRol> usuariosIdWithRolAsNull = empleadoService.listUsuariosIdWithRolAsNull();
        try {
            usuariosIdWithRolAsNull.forEach(ur -> empleadosWithRolAsNull
                    .add(empleadoService.getEmpleadoByUsuarioId(ur.getUsuarioId())));
        } catch (Exception e) {
            throw e;
        }
        return new ResponseEntity<List<Empleado>>(empleadosWithRolAsNull, HttpStatus.OK);
    }

    @GetMapping(path = "/menu-administrador/admin-empleados/consultar/{empleadoNumDocumento}/obtener-id-de-usuario")
    public ResponseEntity<Short> obtenerUsuarioIdDeEmpleado(@PathVariable String empleadoNumDocumento) {
        return new ResponseEntity<Short>(empleadoService.getUsuarioEmpleadoId(empleadoNumDocumento), HttpStatus.OK);
    }

    @PutMapping(path = "/menu-asesor/solicitudes/clientes/{numDocumento}/modificar")
    public ResponseEntity<Cliente> modificarCuentaCliente(
            @PathVariable("numDocumento") String clienteNumDocumento,
            @RequestBody Cliente cliente) {
        final Short usuarioClienteId = clienteService
                .getUsuarioClienteIdByNumDocumento(clienteNumDocumento);
        cliente.setUsuario_id(usuarioClienteId);

        final Cliente savedCliente;
        try {
            savedCliente = clienteService.updateByUsuarioId(cliente.getUsuario_id(), cliente);
        } catch (Exception ex) {
            throw ex;
        }
        return new ResponseEntity<Cliente>(savedCliente, HttpStatus.OK);
    }

    @DeleteMapping(path = "/menu-asesor/solicitudes/clientes/{numDocumento}/eliminar")
    public ResponseEntity<Short> eliminarCuentaCliente(@PathVariable String numDocumento) {
        final short rows;
        final Short usuarioClienteId;
        try {
            usuarioClienteId = clienteService.getUsuarioClienteIdByNumDocumento(numDocumento);
            rows = clienteService.deleteByUsuarioId(usuarioClienteId);
        } catch (Exception ex) {
            throw ex;
        }
        return new ResponseEntity<Short>(rows, HttpStatus.OK);
    }

    @PostMapping(path = "/menu-administrador/servicios/agregar/nuevo")
    public ResponseEntity<String> agregarServicio(@RequestBody() Servicio servicio) throws IOException {
        final Servicio myServicio = servicioService.save(servicio);
        if (myServicio == null) {
            throw new RuntimeException(
                    "El cuerpo de la petición contiene campos de datos con restricción definida por duplicidad, no se guardó el servicio");
        }
        return new ResponseEntity<String>(myServicio.toString(), HttpStatus.CREATED);
    }

    @GetMapping(path = "/menu-administrador/servicios/consultar/{name}")
    public ResponseEntity<Servicio> consultarServicio(@PathVariable("name") String servicioNombre) throws IOException {
        final Servicio servicio = servicioService.getOne(servicioNombre);

        if (servicio != null) {
            return new ResponseEntity<Servicio>(servicio, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("El Servicio consultado no existe");
    }

    @PostMapping(path = "/menu-administrador/admin-servicios/consultar/{name}/modificar")
    public Servicio modificarServicio(@PathVariable("name") String servicioNombre, @RequestBody Servicio servicio)
            throws IOException {
        return servicioService.updateOne(servicioNombre, servicio);
    }

    @DeleteMapping(path = "/menu-administrador/admin-servicios/consultar/{id}/eliminar")
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
            @RequestBody Agendamiento agendamiento) {
        if (agendamiento.getUsuarioClienteId() != null &&
                !agendamiento
                        .getUsuarioClienteId()
                        .equals(clienteService.getUsuarioClienteIdByNumDocumento(numDocumento))) {
            throw new NoSuchElementException(
                    "El id de usuario del cliente consultado no coincide con el del agendamiento");
        }

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

    @GetMapping(path = "/menu-administrador/agendamientos")
    public ResponseEntity<List<Agendamiento>> listarAgendamientos() {
        return new ResponseEntity<>(this.agendamientoService.listAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/menu-asesor/agendamientos/clientes/{numDocumento}/pagados")
    public ResponseEntity<List<Agendamiento>> listarAgendamientosPagadosPorCliente(@PathVariable String numDocumento) {
        return new ResponseEntity<List<Agendamiento>>(
                this.agendamientoService.listPagadosByClienteNumDocumento(numDocumento),
                HttpStatus.OK);
    }

    @GetMapping(path = "/menu-asesor/agendamientos/clientes/{numDocumento}/tomados")
    public ResponseEntity<List<Agendamiento>> listarAgendamientosTomadosPorCliente(@PathVariable String numDocumento) {
        return new ResponseEntity<List<Agendamiento>>(
                this.agendamientoService.listTomadosByClienteNumDocumento(numDocumento),
                HttpStatus.OK);
    }

    @GetMapping(path = "/menu-asesor/reportes/ingresos/clientes/{numDocumento}")
    //@CircuitBreaker(name = "clienteIngresosBreaker", fallbackMethod = "clienteIngresosFallback")
    public ResponseEntity<Map<String, Object>> buscarClienteIngresos(
            @PathVariable("numDocumento") String clienteNumDocumento) {
        final Map<String, Object> map = new HashMap<>();
        // Hayamos el cliente por su número de documento y lo asignamos en el objeto map
        // por separado de la lista de ingresos
        final Cliente cliente = clienteService.getCliente(clienteNumDocumento);
        map.put("cliente", cliente);

        List<Ingreso> ingresosList;
        ingresosList = List.of();
        try {
            ingresosList = ingresoService.searchByClienteNumDocumento(clienteNumDocumento);
        } catch (Exception e) {
            throw new RuntimeException("Se ha enviado un mensaje en el respaldo a fallo de la funcionalidad");
        }

        for (Ingreso ingreso : ingresosList) {
            // Sumamos campos a objeto ingreso actual de la iteración: nombre, precio y url
            // de Servicio correspondiente al servicioId
            final Servicio currServicio = servicioService.getOneById(ingreso.getServicioId());
            ingreso.setServicioNombre(currServicio.getServicioNombre());
            ingreso.setServicioPrecio(currServicio.getPrecio());
            ingreso.setServicioImgUrl(currServicio.getImgUrl());
        }
        map.put("ingresos", ingresosList);

        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    @PostMapping(path = "/menu-asesor/solicitudes/clientes/{numDocumento}/reagendar-servicio")
    public ResponseEntity<Agendamiento> reagendarServicio(
            @PathVariable("numDocumento") String clienteNumDocumento,
            @RequestBody Agendamiento agendamiento) {
        Agendamiento newAgendamiento;
        try {
            if (agendamiento.getUsuarioClienteId()
                    .equals(clienteService.getUsuarioClienteIdByNumDocumento(clienteNumDocumento)) &&
                    agendamiento.getEstado().equals("pago")) {
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
            @PathVariable("agendamientoId") String agendamientoId) {
        final String delAgendamientoId;
        if (!agendamientoService
                .listByUsuarioClienteId(clienteService.getUsuarioClienteIdByNumDocumento(usuarioClienteNumDocumento))
                .isEmpty()) {
            delAgendamientoId = agendamientoService.cancelOneById(agendamientoId);
            if (delAgendamientoId != null) {
                return new ResponseEntity<String>(delAgendamientoId, HttpStatus.OK);
            } else {
                throw new ResourceNotFoundException(
                        "El agendamiento con id del cuerpo de petición para cancelar el servicio no se eliminó porque no existe");
            }
        } else {
            throw new RuntimeException();
        }
    }

    public ResponseEntity<String> clienteIngresosFallback(Short usuarioId, Exception e) {
        log.info("El respaldo se ejecuta porque el servicio está inactivo o caído: ", e);
        return new ResponseEntity<String>("Un respaldo a fallo se ha ejecutado", HttpStatus.OK);
    }
}
