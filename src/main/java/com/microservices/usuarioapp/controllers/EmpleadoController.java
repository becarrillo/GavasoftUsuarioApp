package com.microservices.usuarioapp.controllers;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.entities.Empleado;
import com.microservices.usuarioapp.entities.Usuario;
import com.microservices.usuarioapp.exceptions.ResourceNotFoundException;
import com.microservices.usuarioapp.external.models.Agendamiento;
import com.microservices.usuarioapp.external.models.Factura;
import com.microservices.usuarioapp.external.models.Ingreso;
import com.microservices.usuarioapp.external.models.Servicio;
import com.microservices.usuarioapp.external.services.*;
import com.microservices.usuarioapp.models.UsuarioDto;
import com.microservices.usuarioapp.models.UsuarioRol;
import com.microservices.usuarioapp.responses.ApiResponse;
import com.microservices.usuarioapp.services.ClienteService;
import com.microservices.usuarioapp.services.EmpleadoService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

import org.springframework.http.HttpHeaders;

@RestController
@CrossOrigin(origins = {"https://adso-sena-astin-gavasoft.web.app", "https://adso-sena-astin-gavasoft.firebaseapp.com"}, maxAge = 540L)
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
    private FacturaService facturaService;


    @PostMapping(path = "/menu-administrador/admin-empleados/crear-cuenta/nuevo")
    public ResponseEntity<Empleado> crearCuentaEmpleado(@RequestBody() Empleado empleado) throws SQLException {
        final HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Vary", "Origin");
        return new ResponseEntity<Empleado>(
                empleadoService.save(empleado),
                HttpStatus.CREATED);
    }

    @PostMapping(path = "/menu-administrador/admin-empleados/asignar-rol")
    public ResponseEntity<Short> asignarRol(
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
        final String decodedEmpleadoNombre = URLDecoder.decode(
                empleadoNombre.replace('+', ' '),
                StandardCharsets.UTF_8
        );

        return new ResponseEntity<List<Empleado>>(
                empleadoService.getEmpleadosByNombre(decodedEmpleadoNombre),
                HttpStatus.OK
        );
    }

    @GetMapping(path = "/menu-administrador/admin-empleados/listar/sin-rol")
    public ResponseEntity<List<Empleado>> listarPorRolNull() {
        List<Empleado> empleadosWithRolAsNull = empleadoService.listEmpleadosWithRolAsNull();
        
        return new ResponseEntity<List<Empleado>>(empleadosWithRolAsNull, HttpStatus.OK);
    }

    @GetMapping(path = "/menu-administrador/admin-empleados/consultar/{empleadoNumDocumento}/obtener-id-de-usuario")
    public ResponseEntity<Short> obtenerUsuarioIdDeEmpleado(@PathVariable String empleadoNumDocumento) {
        return new ResponseEntity<Short>(empleadoService.getUsuarioEmpleadoId(empleadoNumDocumento), HttpStatus.OK);
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

    @PostMapping(path = "/menu-administrador/admin-servicios/agregar/nuevo")
    @Retry(name = "RetryAddingServicio", fallbackMethod = "retryAddingServicioFallback")
    public ResponseEntity<Servicio> agregarServicio(@RequestBody Servicio servicio) throws IOException {
        final Servicio myServicio = servicioService.save(servicio);
        if (myServicio == null) {
            throw new RuntimeException(
                "El cuerpo de la petición contiene campos de datos con restricción definida por duplicidad, no se guardó el servicio");
        }
        return new ResponseEntity<Servicio>(myServicio, HttpStatus.CREATED);
    }

    @GetMapping(path = "/menu-administrador/admin-servicios/consultar-por-nombre/{name}")
    @Retry(name = "RetryServicioByNameQuery", fallbackMethod = "servicioByNameQueryFallback")
    public ResponseEntity<Servicio> consultarServicio(@PathVariable("name") String servicioNombre) throws IOException {
        final Servicio servicio = servicioService.getOne(
            URLDecoder.decode(
                servicioNombre.replace('+', ' '),
                StandardCharsets.UTF_8
            )
        );

        if (servicio != null) {
            return new ResponseEntity<Servicio>(servicio, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("El Servicio consultado no existe");
    }

    @PostMapping(path = "/menu-administrador/admin-servicios/consultar/{name}/modificar")
    @Retry(name = "RetryUpdatingServicio", fallbackMethod = "updatingServicioFallback")
    public Servicio modificarServicio(@PathVariable("name") String servicioNombre, @RequestBody Servicio servicio)
            throws IOException {
        return servicioService.updateOne(servicioNombre, servicio);
    }

    @DeleteMapping(path = "/menu-administrador/admin-servicios/consultar/{id}/eliminar")
    @Retry(name = "RetryDeletingServicio", fallbackMethod = "deletingServicioFallback")
    public ResponseEntity<String> eliminarServicio(@PathVariable("id") String servicioId) {
        final String delServiceId = servicioService.deleteOneById(servicioId);
        if (delServiceId != null) {
            return new ResponseEntity<String>(delServiceId, HttpStatus.OK);
        }
        throw new ResourceNotFoundException("El Servicio con el id no existe, no se eliminará registro alguno");
    }

    @GetMapping(path = "/listar-servicios")
    @Retry(name = "RetryListingServicios", fallbackMethod = "listingServiciosFallback")
    public ResponseEntity<List<Servicio>> listarServicios() {
        return new ResponseEntity<List<Servicio>>(servicioService.getAll(), HttpStatus.OK);
    }

    @CircuitBreaker(name = "SchedulingServicioCircuitBreaker", fallbackMethod = "schedulingServicioFallback")
    @PostMapping(path = "/menu-administrador/admin-agenda/agendar-servicio/nuevo")
    public ResponseEntity<Agendamiento> agendarServicioByRoleAdmin(@RequestBody Agendamiento agendamiento) {
        Agendamiento newAgendamiento;

        try {
            newAgendamiento = agendamientoService.save(agendamiento);
            if (newAgendamiento == null) {
                throw new ResourceNotFoundException("El cliente con el id de usuario del cuerpo de petición no existe");
            }
            return new ResponseEntity<Agendamiento>(newAgendamiento, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping(path = "/menu-asesor/solicitudes/clientes/{numDocumento}/agendar-servicio")
    public ResponseEntity<Agendamiento> agendarServicioByRoleAsesor(
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
    @Retry(name = "RetryListingAgendamientos", fallbackMethod = "listingAgendamientosFallback")
    public ResponseEntity<List<Agendamiento>> listarAgendamientos() {
        return new ResponseEntity<>(this.agendamientoService.listAll(), HttpStatus.OK);
    }

    @GetMapping(path = "/menu-asesor/agendamientos/clientes/{numDocumento}/pagados")
    @Retry(name = "RetryListingPaidsAgendamientos", fallbackMethod = "listingPaidsAgendamientosFallback")
    public ResponseEntity<List<Agendamiento>> listarAgendamientosPagadosPorCliente(@PathVariable String numDocumento) {
        return new ResponseEntity<List<Agendamiento>>(
                this.agendamientoService.listPagadosByClienteNumDocumento(numDocumento),
                HttpStatus.OK);
    }

    @GetMapping(path = "/agendamientos/carritos-de-compras/{carritoId}")
    @Retry(name = "RetryListingAgendamientosByCarritoDeCompras", fallbackMethod = "listingAgendamientosFallbackByCarritoDeCompras")
    public ResponseEntity<List<Agendamiento>> listarAgendamientosPorCarritoDeComprasId(
            @PathVariable("carritoId") String carritoDeComprasId
    ) {
        final List<Agendamiento> agendamientos = agendamientoService.listAllByCarritoDeComprasId(carritoDeComprasId);
        if (agendamientos==null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<List<Agendamiento>>(agendamientos, HttpStatus.OK);
    }

    @GetMapping(path = "/menu-asesor/agendamientos/clientes/{numDocumento}/tomados")
    public ResponseEntity<List<Agendamiento>> listarAgendamientosTomadosPorCliente(@PathVariable String numDocumento) {
        return new ResponseEntity<List<Agendamiento>>(
                this.agendamientoService.listTomadosByClienteNumDocumento(numDocumento),
                HttpStatus.OK);
    }

    @CircuitBreaker(name = "clienteIngresosBreaker", fallbackMethod = "clienteIngresosFallback")
    @GetMapping(path = "/menu-asesor/reportes/ingresos/clientes/{numDocumento}")
    public ResponseEntity<Map<String, Object>> buscarClienteIngresos(
            @PathVariable("numDocumento") String clienteNumDocumento
    ) {
        final Map<String, Object> map = new HashMap<>();
        /* Hayamos el cliente por su número de documento y lo asignamos en el objeto map
        por separado de la lista de ingresos*/ 
        final Cliente cliente = clienteService.getCliente(clienteNumDocumento);
        map.put("cliente", cliente);

        List<Factura> facturasList = new ArrayList<Factura>();
        try {
            facturasList = facturaService
                .listPagadasByClienteNumDocumento(cliente.getNum_documento());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        
        List<Ingreso> ingresosList = new ArrayList<Ingreso>();
        List<Agendamiento> fAgendamientos;
        for (int i=0; i < facturasList.size(); i++) {   /*
            * Obtengo la lista de los agendamientos de la actual factura de la iteración median-
            * te su mismo carrito_de_compras _id y se asigna a una variable */
            fAgendamientos = agendamientoService
                    .listAllByCarritoDeComprasId(facturasList.get(i).getCarritoDeComprasId());
            for (int j=0; j < fAgendamientos.size(); j++) {
                final Ingreso ingreso;
                ingreso =  Ingreso.builder()
                        .facturaId(facturasList.get(i).getFacturaId())
                        .fechaHoraDelServicio(fAgendamientos.get(j).getFechaHora())
                        .servicioId(fAgendamientos.get(j).getServicioId())
                        .build();
                ingresosList.add(ingreso);
            }
        }

        for (Ingreso ingreso : ingresosList) {
            /* Sumamos campos a objeto ingreso actual de la iteración: nombre, precio y url
            de Servicio correspondiente al servicioId */
            final Servicio currServicio = servicioService.getOneById(ingreso.getServicioId());
            ingreso.setServicioNombre(currServicio.getServicioNombre());
            ingreso.setServicioPrecio(currServicio.getPrecio());
            ingreso.setServicioImgUrl(currServicio.getImgUrl());
        }
        map.put("ingresos", ingresosList);

        return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
    }

    @PutMapping(path = "/menu-asesor/solicitudes/clientes/{numDocumento}/reagendar-servicio")
    @Retry(name = "RetryReagendarServicio", fallbackMethod = "reagendarServicioFallback")
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
    @Retry(name = "RetryCancelScheduledServicio", fallbackMethod = "cancelScheduledServicioFallback")
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

    public ResponseEntity<ApiResponse> retryAddingServicioFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió agregar el servicio, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> schedulingServicioFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió agendar el Servicio de Spa, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> listingServiciosFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió listar los servicios del Spa, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> updatingServicioFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió modificar el Servicio de Spa, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> deletingServicioFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió eliminar el Servicio de Spa, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> servicioByNameQueryFallback(Exception e) {
        log.info("Calling server no available, this message was printed as fallback", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió obtener el Servicio de Spa por su nombre, posiblemente el servidor llamado estácaído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> listingAgendamientosFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió listar los agendamientos en el Spa, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> listingPaidsAgendamientosFallback(Exception e) {
        log.info("Calling 'AGENDAMIENTO-APP' microservice is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió listar los agendamientos pagos en el Spa, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> listingAgendamientosFallbackByCarritoDeCompras(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió listar agendamientos por id de Carrito de compras, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> reagendarServicioFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió reagendar el Servicio de Spa, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> cancelScheduledServicioFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió cancelar el Servicio de Spa agendado, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> clienteIngresosFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió obtener los ingresos de Spa por cliente, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }
}
