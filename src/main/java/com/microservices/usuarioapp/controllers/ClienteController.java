package com.microservices.usuarioapp.controllers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/clientes")
@Slf4j
public class ClienteController {

    /*@PostMapping("/nuevo")  ESTO VA PARA EL SERVICIO DE EMPLEADOS
    public ResponseEntity<String> crearCuentaCliente(@RequestBody() Cliente cliente) {
        return new ResponseEntity<>("Se insertaron "+clienteService.saveCliente(cliente).toString()+" registros de clientes exitosamente.", HttpStatus.CREATED);
    }

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
