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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.*;
import java.sql.SQLException;
import java.util.List;

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

    @PostMapping(path = "/empleados/menu-asesor/clientes/crear-cuenta/nuevo")
    public ResponseEntity<String> crearCuentaCliente(
            @RequestBody() Cliente cliente
    ) throws SQLException {
        return new ResponseEntity<String>(clienteService.save(cliente)+" registro(s) con éxito de cliente(s)", HttpStatus.CREATED);
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
        return this.agendamientoService.listClienteAgendamientos(numDocumento);
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

    @PostMapping(path = "/empleados/menu-administrador/agendamientos/agregar/nuevo")
    public ResponseEntity<Agendamiento> agregarAgendamiento(@RequestBody Agendamiento agendamiento) {
        final Agendamiento newAgendamiento;
        
        try {
            newAgendamiento = agendamientoService.save(agendamiento);
            return new ResponseEntity<Agendamiento>(newAgendamiento, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @DeleteMapping(path = "/empleados/menu-asesor/agendamientos/cancelar/{agendamientoId}")
    public ResponseEntity<String> cancelarServicioPago(@PathVariable("agendamientoId") String agendamientoId) {
        final String delAgendamientoId = agendamientoService.cancelOneById(agendamientoId);
        if (delAgendamientoId == null) {
            throw new RuntimeException("El agendamiento del servicio de Spa podría no estar pago o no existe");
        } else {
            return new ResponseEntity<String>(delAgendamientoId, HttpStatus.OK);
        }
    }

    /*@PostMapping(path = "/empleados/menu-asesor/agendamientos/reagendar/{agendamientoId}")
    public void reagendarServicio(
            @PathVariable("agendamientoId") String agendamientoId,
            Agendamiento agendamiento
    ) {
         agendamientoService.reagendarServicio(agendamientoId, agendamiento);
    }
     */
}
