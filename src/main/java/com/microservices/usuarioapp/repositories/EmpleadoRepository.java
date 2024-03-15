package com.microservices.usuarioapp.repositories;

import com.microservices.usuarioapp.entities.Empleado;
import com.microservices.usuarioapp.entities.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Repository
public class EmpleadoRepository implements IEmpleadoRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
     * @apiNote (es) Antes de persistir al Empleado en el repositorio (BD) se guarda instancia de Usuario en la
     * tabla usuarios por medio de los campos heredados del constructor de ésta (super).
     */
    @Override
    public Short saveEmpleado(Empleado empleado) {
        String SQL;

        SQL = "IF ? NOT IN (SELECT url_fotografia FROM dbo.empleados) INSERT into dbo.usuarios VALUES(?,?,?,?,?,?)";
        jdbcTemplate.update(
                SQL,
                empleado.getUrl_fotografia(),
                empleado.getApellidos(),
                empleado.getNombre(),
                empleado.getEmail(),
                empleado.getPassword(),
                empleado.getRol(),
                empleado.getTel()
        );

        // Podríamos guardar instancia de empleado pero sin el usuario_id, lo que no es perimitido ya que es un campo NOT NULL
        SQL = "SELECT * FROM dbo.usuarios WHERE email=?";           // entonces hacemos la consulta que nos ayuda a recuperarlo
        final Usuario empleadoUsuario = jdbcTemplate.queryForObject(
                SQL,
                BeanPropertyRowMapper.newInstance(Usuario.class),
                empleado.getEmail()
        );
        assert empleadoUsuario != null;

        // Se persisten los datos de la tabla empleados y se obtiene el número de filas creadas (por defecto es 1 el valor)
        SQL = "IF ? NOT IN (SELECT url_fotografia FROM dbo.empleados) INSERT INTO dbo.empleados VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        return (short) jdbcTemplate.update(
                SQL,
                empleado.getUrl_fotografia(),
                empleadoUsuario.getUsuario_id(),
                empleadoUsuario.getApellidos(),
                empleadoUsuario.getNombre(),
                empleadoUsuario.getEmail(),
                empleadoUsuario.getPassword(),
                empleadoUsuario.getRol(),
                empleadoUsuario.getTel(),
                empleado.getTipo_documento(),
                empleado.getNum_documento(),
                empleado.getUrl_fotografia(),
                empleado.getFecha_entrada(),
                empleado.getFecha_retiro()
        );
    }

    @Override
    public String getEmpleadoRolByUsuarioId(Short usuarioEmpleadoId) {
        String SQL;
        SQL = "SELECT rol FROM dbo.usuarios AS us WHERE us.usuario_id=?";

        return jdbcTemplate.queryForObject(
                SQL,
                BeanPropertyRowMapper.newInstance(String.class),
                usuarioEmpleadoId
        );
    }

    @Override
    public int setEmpleadoRol(Short usuarioId, String rol) {
        String SQL;
        SQL = "UPDATE dbo.usuarios SET rol=? WHERE usuario_id=?";
        jdbcTemplate.update(
                SQL,
                rol,
                usuarioId.toString()
        );

        SQL = "UPDATE dbo.empleados SET rol=? WHERE usuario_id=?";

        final int rows = jdbcTemplate.update(
                SQL,
                rol,
                usuarioId.toString()
        );
        return rows;
    }

    @Override
    public List<Empleado> listAll() {
        final String SQL = "SELECT * from dbo.empleados";
        return jdbcTemplate.query(SQL, BeanPropertyRowMapper.newInstance(Empleado.class));
    }

    @Override
    public Empleado findOneByUsuarioId(Short usuarioId) {
        final String SQL = "SELECT * from dbo.empleados WHERE usuario_id="+usuarioId.toString();
        return jdbcTemplate.queryForObject(SQL, BeanPropertyRowMapper.newInstance(Empleado.class));
    }

    @Override
    public short updateByUsuarioId(short usuarioId, Empleado empleado) {
        final String SQL;
        SQL = "UPDATE dbo.empleados SET apellidos=?, nombre=?, email=?, password=?, rol=?, tel=?, tipo_documento=?, num_documento=?, url_fotografia=?, fecha_entrada=?, fecha_retiro=? WHERE usuario_id=?";
        return (short) jdbcTemplate.update(
                SQL,
                empleado.getApellidos(),
                empleado.getNombre(),
                empleado.getEmail(),
                empleado.getPassword(),
                empleado.getRol(),
                empleado.getTel(),
                empleado.getTipo_documento(),
                empleado.getNum_documento(),
                empleado.getUrl_fotografia(),
                empleado.getFecha_entrada(),
                empleado.getFecha_retiro(),
                usuarioId
        );
    }

    @Override
    public short deleteByUsuarioId(short usuarioId) {
        return 0;
    }

}
