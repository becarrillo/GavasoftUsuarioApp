package com.microservices.usuarioapp.controllers;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
    private AgendamientoService agendamientoService;

    @PostMapping(path = "/clientes/registro")
    public ResponseEntity<String> crearCuenta(@RequestBody() Cliente cliente) {
        try {
            return new ResponseEntity<>("Se insertaron con éxito "+clienteService.save(cliente)+" registro(s) de cliente(s)", HttpStatus.CREATED);
        } catch (Exception e) {
            throw e;
        }
    }

    @GetMapping(path = "/clientes/servicios")
    public ResponseEntity<List<Servicio>> consultarServicios() {
        return new ResponseEntity<List<Servicio>>(servicioService.getAll(), HttpStatus.OK);
    }

    @PostMapping(path = "/clientes/{clienteNumDocumento}/modificar")
    public ResponseEntity<String> modificarCuentaCliente(@PathVariable("clienteNumDocumento") String numDocumento, @RequestBody Cliente cliente) {
        return new ResponseEntity<>(clienteService.updateByUsuario(numDocumento, cliente).toString().concat(" fila(s) afectada(s)"), HttpStatus.OK);
    }


    /*
    public ResponseEntity<List<Factura>> consultarHistorialDeCompras() {

    }
    */

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
