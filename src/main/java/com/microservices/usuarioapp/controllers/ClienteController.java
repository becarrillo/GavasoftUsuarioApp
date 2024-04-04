package com.microservices.usuarioapp.controllers;
import com.microservices.usuarioapp.exceptions.ResourceNotFoundException;
import com.microservices.usuarioapp.external.models.Carrito;
import com.microservices.usuarioapp.external.models.Valoracion;
import com.microservices.usuarioapp.external.services.*;
import com.microservices.usuarioapp.external.models.Factura;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.external.models.Agendamiento;
import com.microservices.usuarioapp.external.models.Servicio;
import com.microservices.usuarioapp.services.ClienteService;

@RestController
@CrossOrigin("*")
@RequestMapping("/usuarios/clientes")
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

    @PostMapping(path = "/registro")
    public ResponseEntity<String> crearCuenta(@RequestBody() Cliente cliente) {
        try {
            return new ResponseEntity<>("Se insertaron con éxito "+clienteService.save(cliente)+" registro(s) de cliente(s)", HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping(path = "/{clienteNumDocumento}/modificar")
    public ResponseEntity<String> modificarCuenta(
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

    @GetMapping(path = "/obtener-usuario-id/{clienteNumDocumento}")
    public Short obtenerUsuarioClienteIdByNumDocumento(@PathVariable String clienteNumDocumento) {
        return clienteService.getUsuarioClienteIdByNumDocumento(clienteNumDocumento);
    }

    @GetMapping(path = "/servicios")
    public ResponseEntity<List<Servicio>> consultarServicios() {
        return new ResponseEntity<List<Servicio>>(servicioService.getAll(), HttpStatus.OK);
    }

    @PostMapping(path = "/valoraciones/servicios/{servicioId}/agregar/nueva")
    public ResponseEntity<String> valorarServicio(
            @PathVariable("servicioId") String servicioId,
            @RequestBody Valoracion valoracion
    ) {
        final String bodyRes = valoracionService.save(servicioId, valoracion);
        return new ResponseEntity<String>(bodyRes, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/valoraciones/{servicioValoracionId}/eliminar")
    public ResponseEntity<String> eliminarValoracionDeServicio(@PathVariable("servicioValoracionId") String servicioValoracionId) {
        final String bodyRes = valoracionService.deleteOneById(servicioValoracionId);
        return new ResponseEntity<String>(bodyRes, HttpStatus.OK);
    }

    @PostMapping(path = "/solicitudes/consultar-agenda/agendar-servicio")
    public ResponseEntity<Agendamiento> agendarServicio(@RequestBody Agendamiento agendamiento) {
        final String carritoDeComprasId = agendamientoService.getCarritoDeComprasIdByUsuarioClienteId(agendamiento.getUsuarioClienteId());
        // Se verifica la NO existencia de un agendamiento con estado "tomado" del cliente (NO pertenece a algún Carrito)
        if (carritoDeComprasId == null) {
            agendamiento.setCarritoDeComprasId(carritoService.create()); // Se asocia el agendamiento a instancia nueva de Carrito
        } else {
            agendamiento.setCarritoDeComprasId(carritoDeComprasId);
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

    // Lo que se cancela es el Agendamiento, aunque el Servicio es lo que se compra en el Spa
    @DeleteMapping(
            path="/menu-cliente/historial-compras/clientes/{clienteNumDocumento}/agendamientos/{agendamientoId}/cancelar"
    )
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
            throw new RuntimeException("El agendamiento del servicio de Spa podría estar sin pago, estar dentro de 24 horas antes o no existir");
        }
    }

    @GetMapping(path = "/{usuarioClienteId}/carrito-de-compras")
    public ResponseEntity<Carrito> consultarCarritoDeComprasPorUsuarioClienteId(
            @PathVariable Short usuarioClienteId
    ) {
        /*
            Cada agendamiento: cuando su estado es "tomado" por defecto, le pertenece a
            un carrito de compras antes de ser cambiado a "facturado". Por lo que el usua-
            rio tendrá siempre disponible un solo carrito de compras al agregar ítems
        */
        final List<Agendamiento> agendamientosTomados = agendamientoService
                .listByUsuarioClienteId(usuarioClienteId)
                .stream()
                .filter(at -> at.getEstado().equals("tomado"))
                .toList();
        if (agendamientosTomados.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        final Agendamiento firstTomadoAgendamiento = agendamientosTomados.get(0);
        final Carrito foundCarrito = carritoService.getOne(firstTomadoAgendamiento.getCarritoDeComprasId());
        foundCarrito.setAgendamientosList(agendamientosTomados);

        return new ResponseEntity<Carrito>(
                foundCarrito,
                HttpStatus.OK
        );
    }

    @PostMapping(path = "/{usuarioClienteId}/facturas/generar-nueva")
    public ResponseEntity<Factura> generarFactura(@PathVariable Short usuarioClienteId, @RequestBody Factura factura) {
        try {
            final Factura generatedFactura = facturaService.generate(usuarioClienteId, factura);
            ResponseEntity<Factura> res;

            if (generatedFactura != null) {
                res = new ResponseEntity<Factura>(
                        generatedFactura,
                        HttpStatus.CREATED
                );

                Objects.requireNonNull(res.getBody())
                        .setAgendamientosList(agendamientoService
                                .setEstadoToFacturado(
                                        factura.getCarritoDeComprasId()
                                )
                        );
                return res;
            }
            res = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            return res;
        } catch (Exception e) {
            throw e;
        }
    }

    /*
    @GetMapping(path = "/{usuario-id}/ingresos-cliente")
    @CircuitBreaker(name = "clienteIngresosBreaker", fallbackMethod = "clienteIngresosFallback")
    public ResponseEntity<List<Ingreso>>
    */

    public ResponseEntity<String> clienteIngresosFallback(Short usuarioId, Exception e) {
        log.info("El respaldo se ejecuta porque el servicio está inactivo o caído: ", e);
        return new ResponseEntity<String>("Un respaldo a fallo se ha ejecutado", HttpStatus.OK);
    }
}
