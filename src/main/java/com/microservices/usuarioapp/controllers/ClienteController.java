package com.microservices.usuarioapp.controllers;
import com.microservices.usuarioapp.exceptions.ResourceNotFoundException;
import com.microservices.usuarioapp.external.models.Carrito;
import com.microservices.usuarioapp.external.models.Valoracion;
import com.microservices.usuarioapp.external.services.CarritoService;
import com.microservices.usuarioapp.external.services.ValoracionService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.external.models.Agendamiento;
import com.microservices.usuarioapp.external.models.Servicio;
import com.microservices.usuarioapp.external.services.AgendamientoService;
import com.microservices.usuarioapp.external.services.ServicioService;
import com.microservices.usuarioapp.services.ClienteService;

@RestController
@CrossOrigin("*")
@RequestMapping("/usuarios")
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

    @PostMapping(path = "/clientes/registro")
    public ResponseEntity<String> crearCuenta(@RequestBody() Cliente cliente) {
        try {
            return new ResponseEntity<>("Se insertaron con éxito "+clienteService.save(cliente)+" registro(s) de cliente(s)", HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping(path = "/clientes/{clienteNumDocumento}/modificar")
    public ResponseEntity<String> modificarCuentaCliente(@PathVariable("clienteNumDocumento") String numDocumento, @RequestBody Cliente cliente) {
        return new ResponseEntity<>(clienteService.updateByUsuario(numDocumento, cliente).toString().concat(" fila(s) afectada(s)"), HttpStatus.OK);
    }

    @GetMapping(path = "/clientes/servicios")
    public ResponseEntity<List<Servicio>> consultarServicios() {
        return new ResponseEntity<List<Servicio>>(servicioService.getAll(), HttpStatus.OK);
    }

    @PostMapping(path = "/clientes/valoraciones/servicios/{servicioId}/agregar/nueva")
    public ResponseEntity<String> valorarServicio(
            @PathVariable("servicioId") String servicioId,
            @RequestBody Valoracion valoracion
    ) {
        final String bodyRes = valoracionService.save(servicioId, valoracion);
        return new ResponseEntity<String>(bodyRes, HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/clientes/valoraciones/{servicioValoracionId}/eliminar")
    public ResponseEntity<String> eliminarValoracionDeServicio(@PathVariable("servicioValoracionId") String servicioValoracionId) {
        final String bodyRes = valoracionService.deleteOneById(servicioValoracionId);
        return new ResponseEntity<String>(bodyRes, HttpStatus.OK);
    }

    @PostMapping(path = "/clientes/solicitudes/consultar-agenda/agendar-servicio")
    public ResponseEntity<Agendamiento> agendarServicio(@RequestBody Agendamiento agendamiento) {
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
                carritoService.addSubtotal(newAgendamiento.getCarritoDeComprasId(), newAgendamiento.getServicioId());
            }
            return new ResponseEntity<Agendamiento>(newAgendamiento, HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    // Lo que se cancela es el Agendamiento, aunque el Servicio es lo que se compra en el Spa
    @DeleteMapping(path = "/clientes/menu-cliente/historial-compras/clientes/{clienteNumDocumento}/agendamientos/{agendamientoId}/cancelar")
    public ResponseEntity<String> cancelarServicioPago(
            @PathVariable("clienteNumDocumento") String clienteNumDocumento,
            @PathVariable("agendamientoId") String agendamientoId
    ) {
        final Agendamiento agendamiento = agendamientoService.getOneById(agendamientoId);
        final Cliente cliente = clienteService.getCliente(clienteNumDocumento);
        // Validaciones
        if (
                agendamiento.getUsuarioClienteId().equals(cliente.getUsuario_id()) &&
                        agendamiento.getUsuarioClienteId().equals(clienteService.getUsuarioClienteIdByNumDocumento(clienteNumDocumento)) &&
                        agendamiento.getEstado().equals("pago") &&
                        // El sistema permitirá cancelar el servicio agendado desde 24 hrs. antes de su prestación
                        LocalDateTime.now(ZoneId.of("GMT-5")).isBefore(agendamiento.getFechaHora().minusHours(24)) &&
                        // El sistema verifica nuevamente la autorización del cliente al Spa para el manejo de los datos
                        cliente.isAutorizacion_datos()
        ) {
            final Agendamiento cancelled = agendamientoService.cancelOnePaidById(agendamientoId);
            return new ResponseEntity<String>("Servicio cancelado en agendamiento '"+cancelled.toString().concat("'"), HttpStatus.OK);
        } else {
            throw new RuntimeException("El agendamiento del servicio de Spa podría no estar pago o no existe");
        }
    }

    @GetMapping(path = "/clientes/carritos-de-compras/{carritoDeComprasId}")
    public ResponseEntity<Carrito> consultarCarritoDeCompras(@PathVariable("carritoDeComprasId") String carritoDeComprasId) {
        final Carrito foundCarrito = carritoService.getOne(carritoDeComprasId);

        if (foundCarrito == null) {
            throw new ResourceNotFoundException();
        }
        return new ResponseEntity<Carrito>(foundCarrito, HttpStatus.OK);
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
