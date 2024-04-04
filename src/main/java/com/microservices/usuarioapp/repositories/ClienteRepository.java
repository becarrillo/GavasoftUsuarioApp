package com.microservices.usuarioapp.repositories;

import com.microservices.usuarioapp.entities.Cliente;
import com.microservices.usuarioapp.entities.Usuario;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Repository
@Slf4j
public class ClienteRepository implements IClienteRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public int save(Cliente cliente) {
        cliente.setRol("cliente");    // El rol agregado manualmente a la instancia para no persistirla con rol NULL por defecto

        byte isAuthDatosAsByte;
        if (cliente.isAutorizacion_datos()) {
            isAuthDatosAsByte = 1;
        } else { isAuthDatosAsByte = 0; }

        String SQL;
        SQL = "IF ? != 0 INSERT into dbo.usuarios VALUES(?,?,?,?,?,?)";
        jdbcTemplate.update(
                SQL,
                isAuthDatosAsByte,
                cliente.getApellidos(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getPassword(),
                cliente.getRol(),
                cliente.getTel()
        );

        // Podríamos guardar instancia de cliente pero sin el usuario_id, lo que no es perimitido ya que es un campo NOT NULL
        SQL = "SELECT * FROM dbo.usuarios WHERE email=?";
        final Usuario clienteUsuario = jdbcTemplate.queryForObject(
                SQL,
                BeanPropertyRowMapper.newInstance(Usuario.class),
                cliente.getEmail()
        );
        assert clienteUsuario != null;
        cliente.setFecha_hora_registro(LocalDateTime.now(ZoneId.of("GMT-5")));  // Hora local (Bogotá) en tiempo real (Timestamp)

        // Se persisten los datos de la tabla clientes y se obtiene el número de filas creadas (por defecto es 1 el valor)
        SQL = "IF ? != 0 INSERT INTO dbo.clientes VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        return jdbcTemplate.update(
                SQL,
                isAuthDatosAsByte,
                clienteUsuario.getUsuario_id(),
                clienteUsuario.getApellidos(),
                clienteUsuario.getNombre(),
                clienteUsuario.getEmail(),
                clienteUsuario.getPassword(),
                clienteUsuario.getRol(),
                clienteUsuario.getTel(),
                cliente.getTipo_documento(),
                cliente.getNum_documento(),
                isAuthDatosAsByte,
                cliente.getSaldo_favor(),
                cliente.getFecha_hora_registro()
        );
    }

    @Override
    public List<Cliente> listAll() {
        final String SQL = "SELECT * FROM dbo.usuarios";
        return jdbcTemplate.query(SQL, BeanPropertyRowMapper.newInstance(Cliente.class));
    }

    @Override
    public short getUsuarioId(String numDocumento) {
        final String SQL = "SELECT * from dbo.clientes WHERE num_documento=?";
        final Cliente foundCliente = jdbcTemplate.queryForObject(SQL, BeanPropertyRowMapper.newInstance(Cliente.class), numDocumento);
        assert foundCliente != null;
        log.info("Cliente del servicio agendado hallado: '{}'", foundCliente.getEmail().concat(" con nombre: ").concat(foundCliente.getNombre()+" y apellidos: ").concat(foundCliente.getApellidos()));
        return Objects.requireNonNull(foundCliente).getUsuario_id();
    }

    @Override
    public Cliente findOne(String numDocumento) {
        final String SQL = "SELECT * from dbo.clientes WHERE num_documento=?";
        final Cliente bodyRes;
        try {
            bodyRes =  jdbcTemplate.queryForObject(SQL, BeanPropertyRowMapper.newInstance(Cliente.class), numDocumento);
        } catch (Exception ex) {
            throw ex;
        }

        // Todos los objetos de clientes en BD deben estar con autorizacion_datos=1, traduce que el cliente autorizó
        assert bodyRes != null;
        bodyRes.setAutorizacion_datos(true);
        return bodyRes;
    }

    @Override
    public Cliente findOneByUsuarioId(Short usuarioId) {
        final String SQL = "SELECT * from dbo.clientes WHERE usuario_id=?";
        return jdbcTemplate.queryForObject(SQL, BeanPropertyRowMapper.newInstance(Cliente.class), usuarioId.toString());
    }

    @Override
    public Short findUsuarioClienteIdByNumDocumento(String numDocumento) {
        String SQL;
        SQL = "SELECT usuario_id from dbo.clientes WHERE num_documento=?";

        return jdbcTemplate.queryForObject(SQL, Short.class, numDocumento);
    }

    @Override
    public String getClienteNumDocumentoByUsuarioId(Short usuarioClienteId) {
        final String SQL = "SELECT num_documento from dbo.clientes WHERE usuario_id=?";
        return jdbcTemplate.queryForObject(SQL,
                String.class,
                usuarioClienteId
        );
    }

    @Override
    public String getClienteTipoDocumentoByUsuarioId(Short usuarioClienteId) {
        final String SQL = "SELECT tipo_documento from dbo.clientes WHERE usuario_id=?";
        return jdbcTemplate.queryForObject(SQL,
                String.class,
                usuarioClienteId
        );
    }

    @Override
    public String getClienteNombreByUsuarioId(Short usuarioClienteId) {
        final String SQL = "SELECT nombre from dbo.clientes WHERE num_documento=?";
        return jdbcTemplate.queryForObject(SQL,
                String.class,
                usuarioClienteId
        );
    }

    @Override
    public String getClienteApellidosByUsuarioId(Short usuarioClienteId) {
        final String SQL = "SELECT apellidos from dbo.clientes WHERE usuario_id=?";
        return jdbcTemplate.queryForObject(SQL,
                String.class,
                usuarioClienteId
        );
    }

    @Override
    public Cliente updateByUsuarioId(Short usuarioClienteId, Cliente cliente) {
        String SQL = "UPDATE dbo.clientes SET cliente_apellidos=?, cliente_nombre=?, email=?, password=?, rol=?, tel=?, tipo_documento=?, num_documento=?, autorizacion_datos=?, saldo_favor=? WHERE usuario_id=?";
        jdbcTemplate.update(
                SQL,
                cliente.getApellidos(),
                cliente.getNombre(),
                cliente.getEmail(),
                cliente.getPassword(),
                cliente.getRol(),
                cliente.getTel(),
                cliente.getTipo_documento(),
                cliente.getNum_documento(),
                cliente.isAutorizacion_datos(),
                cliente.getSaldo_favor(),
                usuarioClienteId
        );

        SQL = "SELECT * from dbo.clientes WHERE usuario_id=?";
        return jdbcTemplate.queryForObject(SQL, BeanPropertyRowMapper.newInstance(Cliente.class), usuarioClienteId);
    }

    @Override
    public short deleteByUsuarioId(Short usuarioId) {
        final String SQL;
        SQL = "DELETE FROM dbo.usuarios WHERE usuario_id=?";
        return (short) jdbcTemplate.update(SQL, usuarioId);
    }

    /*
    @Override
    public Collection<Factura> getHistorialByUsuarioId(Short usuarioId) {

    }
     */
}
