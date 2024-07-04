package com.microservices.usuarioapp.controllers;
import com.microservices.usuarioapp.exceptions.ResourceNotFoundException;
import com.microservices.usuarioapp.external.models.*;
import com.microservices.usuarioapp.external.services.*;
import com.microservices.usuarioapp.responses.ApiResponse;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.services.ClienteService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@RestController
@CrossOrigin(origins = {"https://adso-sena-astin-gavasoft.web.app", "https://adso-sena-astin-gavasoft.firebaseapp.com"}, maxAge = 540L)
@RequestMapping("/v1/usuarios/clientes")
@Slf4j
public class ClienteController {
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private ValoracionService valoracionService;

    @Autowired
    private AgendamientoService agendamientoService;

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private FacturaService facturaService;

    @PostMapping(path = "/crear-cuenta/nuevo")
    public ResponseEntity<String> crearCuentaCliente(@RequestBody() Cliente cliente) {
        try {
            return new ResponseEntity<>(
                    "Se insertaron con éxito "+clienteService.save(cliente)+" registro(s) de cliente(s)",
                    HttpStatus.CREATED
            );
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping(path = "/{numDocumento}")
    public ResponseEntity<Cliente> consultarCliente(@PathVariable("numDocumento") String clienteNumDocumento) {
        final Cliente cliente = clienteService.getOne(clienteNumDocumento);

        if (cliente == null) {
            throw new ResourceNotFoundException("El cliente no existe");
        }
        return new ResponseEntity<>(cliente, HttpStatus.OK);
    }

    @PutMapping(path = "/{clienteNumDocumento}/modificar")
    public ResponseEntity<String> modificarCuentaCliente(
            @PathVariable("clienteNumDocumento") String numDocumento,
            @RequestBody Cliente cliente
    ) {
        final Short usuarioClienteId;
        usuarioClienteId = clienteService.getUsuarioClienteIdByNumDocumento(numDocumento);
        return new ResponseEntity<>(clienteService.updateByUsuarioId(usuarioClienteId, cliente).toString().concat(" fila(s) afectada(s)"), HttpStatus.OK);
    }

    @GetMapping(path = "/{usuarioClienteId}/numero-de-documento")
    public ResponseEntity<String> obtenerClienteNumDocumentoByUsuarioId(@PathVariable Short usuarioClienteId) {
        return new ResponseEntity<String>(clienteService.getClienteNumDocumentoByUsuarioId(usuarioClienteId), HttpStatus.OK);
    }

    @GetMapping(path = "/{usuarioClienteId}/tipo-de-documento")
    public String obtenerClienteTipoDocumentoByUsuarioId(@PathVariable Short usuarioClienteId) {
        return clienteService.getClienteTipoDocumentoByUsuarioId(usuarioClienteId);
    }

    @GetMapping(path = "/{usuarioClienteId}/apellidos")
    public String obtenerClienteApellidosByUsuarioId(@PathVariable Short usuarioClienteId) {
        return clienteService.getClienteApellidosByUsuarioId(usuarioClienteId);
    }

    @GetMapping(path = "/{usuarioClienteId}/nombre")
    public String obtenerClienteNombreByUsuarioId(@PathVariable Short usuarioClienteId) {
        return clienteService.getClienteNombreByUsuarioId(usuarioClienteId);
    }

    @GetMapping(path = "/{clienteNumDocumento}/obtener-usuario-id")
    public Short obtenerUsuarioClienteIdByNumDocumento(@PathVariable String clienteNumDocumento) {
        return clienteService.getUsuarioClienteIdByNumDocumento(clienteNumDocumento);
    }

    @GetMapping(path = "/listar")
        public ResponseEntity<List<Cliente>> listarClientes() {
            return new ResponseEntity<List<Cliente>>(
                clienteService.listAllClientes(),
                HttpStatus.OK
            );
        }

    @GetMapping(path = "/listar-servicios")
    @Retry(name = "RetryListingServicios", fallbackMethod = "listingServiciosFallback")
    public ResponseEntity<List<Servicio>> listarServicios() {
        return new ResponseEntity<List<Servicio>>(servicioService.getAll(), HttpStatus.OK);
    }

    @PostMapping(path = "/valoraciones/servicios/{servicioId}/agregar/nueva")
    @Retry(name = "RetryValoratingServicio", fallbackMethod = "valoratingServicioFallback")
    public ResponseEntity<String> valorarServicio(
            @PathVariable("servicioId") String servicioId,
            @RequestBody Valoracion valoracion
    ) {
        final String bodyRes = valoracionService.save(servicioId, valoracion);
        return new ResponseEntity<String>(bodyRes, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/valoraciones/{servicioValoracionId}/eliminar")
    @Retry(name = "RetryDeletingServicio", fallbackMethod = "deletingServicioFallback")
    public ResponseEntity<String> eliminarValoracionDeServicio(@PathVariable("servicioValoracionId") String servicioValoracionId) {
        final String bodyRes = valoracionService.deleteOneById(servicioValoracionId);
        return new ResponseEntity<String>(bodyRes, HttpStatus.OK);
    }

    @PostMapping(path = "/solicitudes/agendar-servicio")
    @CircuitBreaker(name = "SchedulingServicioCircuitBreaker", fallbackMethod = "schedulingServicioFallback")
    public ResponseEntity<Agendamiento> agendarServicio(@RequestBody Agendamiento agendamiento) {
        final Carrito foundCarrito = carritoService.getOne(agendamiento.getCarritoDeComprasId());
        // Se verifica la NO existencia de un agendamiento con estado "tomado" del cliente (NO pertenece a algún Carrito)
        if (foundCarrito == null) {
            agendamiento.setCarritoDeComprasId(carritoService.create()); // Se asocia el agendamiento a instancia nueva de Carrito
        } else {
            agendamiento.setCarritoDeComprasId(foundCarrito.getCarritoId());
        }
        final Agendamiento newAgendamiento;

        try {
            newAgendamiento = agendamientoService.save(agendamiento);
            if (newAgendamiento == null) {
                throw new ResourceNotFoundException(
                        "El cliente con el id de usuario de la petición no existe o no tiene ningún agendamiento con estado tomado"
                );
            };
        } catch (Exception e) {
            throw e;
        }
        carritoService.addSubtotal(newAgendamiento.getCarritoDeComprasId(), newAgendamiento.getServicioId());
        return new ResponseEntity<Agendamiento>(newAgendamiento, HttpStatus.CREATED);
    }
    
    @GetMapping(path = "/menu-cliente/agendamientos/consultar-por-fecha-hora/{fechaHora}")
    @Retry(name = "RetryQueryAgendamientoByDateTime", fallbackMethod = "queryAgendamientoByDateTimeFallback")
    public ResponseEntity<Agendamiento> obtenerAgendamientoPorFechaHora(@PathVariable LocalDateTime fechaHora) {
        return new ResponseEntity<Agendamiento>(
                this.agendamientoService.getOneByFechaHora(fechaHora),
                HttpStatus.OK
        );
    }

    // Lo que se cancela es el Agendamiento, aunque el Servicio es lo que se compra en el Spa
    @DeleteMapping(
            path="/menu-cliente/historial-compras/clientes/{clienteNumDocumento}/agendamientos/{agendamientoId}/cancelar"
    )
    @Retry(name = "RetryCancelPaidServicio", fallbackMethod = "cancelPaidServicioFallback")
    public ResponseEntity<String> cancelarServicioPago(
            @PathVariable("clienteNumDocumento") String clienteNumDocumento,
            @PathVariable("agendamientoId") String agendamientoId
    ) {
        final Agendamiento agendamiento = agendamientoService.getOneById(agendamientoId);
        final Cliente cliente = clienteService.getCliente(clienteNumDocumento);
        // Validaciones
        if (
                agendamiento.getUsuarioClienteId().equals(cliente.getUsuario_id()) &&
                        agendamiento.getUsuarioClienteId()
                                .equals(clienteService.getUsuarioClienteIdByNumDocumento(clienteNumDocumento)) &&
                        agendamiento.getEstado().equals("pago") &&
                        // El sistema permitirá cancelar el servicio agendado desde 24 hrs. antes de su prestación
                        LocalDateTime.now(ZoneId.of("GMT-5")).isBefore(agendamiento.getFechaHora().minusHours(24)) &&
                        // El sistema verifica nuevamente la autorización del cliente al Spa para el manejo de los datos
                        cliente.isAutorizacion_datos()
        ) {
            final Agendamiento cancelled = agendamientoService.cancelOnePaidById(agendamientoId);
            return new ResponseEntity<String>("Servicio cancelado en agendamiento '"+cancelled.toString().concat("'"), HttpStatus.OK);
        } else {
            throw new RuntimeException(
                "El agendamiento del servicio de Spa podría estar sin pago, estar dentro de 24 horas antes o no existir"
            );
        }
    }

    @GetMapping(path = "/{clienteNumDocumento}/carrito-de-compras")
    public ResponseEntity<Carrito> consultarCarritoDeComprasPorClienteNumDocumento(
            @PathVariable String clienteNumDocumento
    ) {
        /*
            Cada agendamiento: cuando su estado es "tomado" por defecto, le pertenece a
            un carrito de compras antes de ser cambiado a "facturado". Por lo que el usua-
            rio tendrá siempre disponible un solo carrito de compras al agregar ítems
        */
        final Carrito foundCarrito;

        try {
            foundCarrito = carritoService.getOne(
                agendamientoService
                    .getCarritoDeComprasIdByUsuarioClienteId(
                        clienteService.getUsuarioClienteIdByNumDocumento(clienteNumDocumento)
                    )
            );
            return new ResponseEntity<Carrito>(
                foundCarrito,
                HttpStatus.OK
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(path = "/facturas/generar-nueva")
    @CircuitBreaker(name = "GeneratingFacturaCircuitBreaker", fallbackMethod = "generatingFacturaFallback")
    public ResponseEntity<Factura> generarFactura(@RequestBody Factura factura) {
        final Short PORCENTAJEIVA = 19;   // Definición de porcentaje de IVA
        ResponseEntity<Factura> res = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        
        try {
            final Carrito carrito = carritoService.getOne(factura.getCarritoDeComprasId());
            if (factura.getConceptoDePago().equals("carrito de compras")) {
                factura.setIva((short)((short)(carrito.getSubtotal()) * PORCENTAJEIVA / 100));
                factura.setTotal(carrito.getSubtotal() + ((short)(factura.getIva())));
            
                agendamientoService.setEstadoToFacturado(
                        carrito.getCarritoId()
                );
            }
            AtomicInteger subtotal = new AtomicInteger();
            if (factura.getConceptoDePago().equals("excedente")) {   
                agendamientoService
                    .listAllByCarritoDeComprasId(carrito.getCarritoId())
                    .forEach(a -> subtotal.set(subtotal.get() + servicioService.getOneById(a.getServicioId()).getPrecio()));
                final int PREVCARRITOVERSIONTOTAL = subtotal.get() * ((int) PORCENTAJEIVA) / 100;
                final int NEWCARRITOVERSIONTOTAL = factura.getTotal();
                
                // Tomado el total nuevo, se le resta el de la factura previa traída en el Request Body
                factura.setTotal(NEWCARRITOVERSIONTOTAL - PREVCARRITOVERSIONTOTAL); // Obteniendo excedente e IVA
                factura.setIva((short) (factura.getTotal() * PORCENTAJEIVA / 100));
            }
            final Factura generatedFactura = facturaService.generate(factura);

            if (generatedFactura != null) {
                res = new ResponseEntity<Factura>(
                        generatedFactura,
                        HttpStatus.CREATED
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public ResponseEntity<ApiResponse> listingServiciosFallback(Exception e) {
        log.info("Calling 'SERVICIO-APP' microservice is fallen or inactive", e);
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

    public ResponseEntity<ApiResponse> valoratingServicioFallback(Exception e) {
        log.info("Calling 'SERVICIO-APP' microservice is fallen or inactive", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió valorar el servicio de Spa, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> deletingServicioFallback(Exception e) {
        log.info("Calling 'SERVICIO-APP' microservice is fallen or inactive", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió eliminar el Servicio del Spa, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }
    public ResponseEntity<ApiResponse> schedulingServicioFallback(Exception e) {
        log.info("Any calling microservice is fallen or inactive", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió agendar el Servicio de Spa, alguno de los servidores está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> cancelPaidServicioFallback(Exception e) {
        log.info("Calling microservice is fallen or inactive", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió cancelar el servicio de Spa, posiblemente un servidor llamado está caído o inactivo")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }
    
    public ResponseEntity<ApiResponse> queryAgendamientoByDateTimeFallback(Exception e) {
        log.info("Calling server is fallen perhaps", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió obtener el agendamiento por su fecha-hora, posiblemente el servidor llamado está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }

    public ResponseEntity<ApiResponse> generatingFacturaFallback(Exception e) {
        log.info("Any calling microservice is fallen or inactive", e);
        return new ResponseEntity<ApiResponse>(
            ApiResponse
                .builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message("Un fallo no permitió generar la Factura, alguno de los servidores está caído")
                .success(false)
                .build(),
            HttpStatus.OK
        );
    }
}
