package com.microservices.usuarioapp.repositories;

import com.microservices.usuarioapp.entities.Empleado;
import com.microservices.usuarioapp.entities.Usuario;
import com.microservices.usuarioapp.models.UsuarioRol;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brando Elí Carrillo Pérez
 */
@Repository
public class EmpleadoRepository implements IEmpleadoRepository {
        @Autowired
        private JdbcTemplate jdbcTemplate;

        /**
         * @apiNote (es) Antes de persistir al Empleado en el repositorio (BD) se guarda
         *          instancia de Usuario en la
         *          tabla usuarios por medio de los campos heredados del constructor de
         *          ésta (super).
         */
        @Override
        public Empleado save(Empleado empleado) {
                String SQL;

                SQL = "IF ? NOT IN (SELECT url_fotografia FROM dbo.empleados) INSERT into dbo.usuarios (apellidos, nombre, email, password, rol, tel) VALUES(?,?,?,?,?,?)";
                jdbcTemplate.update(
                                SQL,
                                empleado.getUrl_fotografia(),
                                empleado.getApellidos(),
                                empleado.getNombre(),
                                empleado.getEmail(),
                                empleado.getPassword(),
                                empleado.getRol(),
                                empleado.getTel());

                // Podríamos guardar instancia de empleado pero sin el usuario_id, lo que no es
                // perimitido ya que es un campo NOT NULL
                SQL = "SELECT * FROM dbo.usuarios WHERE email=?"; // entonces hacemos la consulta que nos ayuda a
                                                                  // recuperarlo
                final Usuario empleadoUsuario = jdbcTemplate.queryForObject(
                                SQL,
                                BeanPropertyRowMapper.newInstance(Usuario.class),
                                empleado.getEmail());
                assert empleadoUsuario != null;

                // Se persisten los datos de la tabla empleados y se obtiene el número de filas
                // creadas (por defecto es 1 el valor)
                SQL = "IF ? NOT IN (SELECT url_fotografia FROM dbo.empleados) INSERT INTO dbo.empleados VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
                jdbcTemplate.update(
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
                                empleado.getFecha_retiro());
                return empleado;
        }

        @Override
        public String getEmpleadoRolByUsuarioId(Short usuarioEmpleadoId) {
                String SQL;
                SQL = "SELECT rol FROM dbo.usuarios AS us WHERE us.usuario_id=?";

                return jdbcTemplate.queryForObject(
                                SQL,
                                BeanPropertyRowMapper.newInstance(String.class),
                                usuarioEmpleadoId);
        }

        @Override
        public Usuario getEmpleadoUsuarioByEmail(String email) {
                String SQL;
                SQL = "SELECT * FROM dbo.usuarios WHERE email=?";

                return jdbcTemplate.queryForObject(
                        SQL,
                        BeanPropertyRowMapper.newInstance(Usuario.class),
                        email
                );
        }

        @Override
        public Short getUsuarioIdByNumDocumento(String numDocumento) {
                String SQL = "SELECT usuario_id FROM dbo.empleados WHERE num_documento=?";

                return jdbcTemplate.queryForObject(
                        SQL,
                        Short.class,
                        numDocumento
                );
        }

        @Override
        public short setEmpleadoRol(Short usuarioId, String rol) {
                String SQL = "UPDATE dbo.usuarios SET rol=? WHERE usuario_id=?";

                jdbcTemplate.update(
                        SQL,
                        rol,
                        usuarioId
                );

                SQL = "UPDATE dbo.empleados SET rol=? WHERE usuario_id=?";
                final short rows = (short) jdbcTemplate.update(
                        SQL,
                        rol,
                        usuarioId
                );
                return rows;
        }

        @Override
        public List<Empleado> listAll() {
                final String SQL = "SELECT * from dbo.empleados";
                return jdbcTemplate.query(SQL, BeanPropertyRowMapper.newInstance(Empleado.class));
        }

        @Override
        public List<UsuarioRol> listOnlyUsuarioRolAllWithRolAsNull() {
                List<UsuarioRol> usuariosRolList = new ArrayList<UsuarioRol>();
                
                final String SQL = "SELECT usuario_id FROM dbo.empleados WHERE rol=NULL";
                final List<Short> empleadosWithoutRolUsuarioId = jdbcTemplate.query(
                        SQL,
                        BeanPropertyRowMapper.newInstance(Short.class)
                );

                for (var usuarioId : empleadosWithoutRolUsuarioId) {
                        usuariosRolList.add(
                                new UsuarioRol(usuarioId, null)
                        );
                }
                return usuariosRolList;
        }

        @Override
        public Empleado findOneByUsuarioId(Short usuarioId) {
                final String SQL = "SELECT * from dbo.empleados WHERE usuario_id=" + usuarioId.toString();
                return jdbcTemplate.queryForObject(SQL, BeanPropertyRowMapper.newInstance(Empleado.class));
        }

        @Override
        public Empleado findOneByEmail(String email) {
                final String SQL = "SELECT * FROM dbo.empleados WHERE email=?";
                return jdbcTemplate.queryForObject(SQL,BeanPropertyRowMapper.newInstance(Empleado.class),email);
        }

        @Override
        public List<Empleado> listByNombre(String nombre) {
                final String URL = "SELECT * from dbo.empleados WHERE nombre LIKE ?";
                return jdbcTemplate.query(
                                URL,
                                BeanPropertyRowMapper.newInstance(Empleado.class),
                                nombre.concat("%"));
        }

        @Override
        public Short updateByUsuarioId(short usuarioId, Empleado empleado) {
                String SQL;
                SQL = "UPDATE dbo.usuarios " +
                                "SET apellidos=?, " +
                                "nombre=?, " +
                                "email=?, " +
                                "password=?, " +
                                "rol=?, " +
                                "tel=? WHERE usuario_id=?";
                jdbcTemplate.update(
                                SQL,
                                empleado.getApellidos(),
                                empleado.getNombre(),
                                empleado.getEmail(),
                                empleado.getPassword(),
                                empleado.getRol(),
                                empleado.getTel(),
                                empleado.getUsuario_id()
                );
                
                SQL = "UPDATE dbo.empleados " +
                                "SET apellidos=?, " +
                                "nombre=?, " +
                                "email=?, " +
                                "password=?, " +
                                "rol=?, " +
                                "tel=?, " +
                                "tipo_documento=?, " +
                                "num_documento=?, " +
                                "url_fotografia=?, " +
                                "fecha_entrada=?, " +
                                "fecha_retiro=? WHERE usuario_id=?";
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
        public Short deleteByUsuarioId(short usuarioId) {
                String SQL;
                SQL = "DELETE FROM dbo.usuarios WHERE usuario_id=?";
                return (short) jdbcTemplate.update(SQL, usuarioId);
        }

        @Override
        public Empleado findOne(String numDocumento) {
                final String SQL = "SELECT * from dbo.empleados WHERE num_documento=?";
                return jdbcTemplate.queryForObject(SQL, BeanPropertyRowMapper.newInstance(Empleado.class),
                                numDocumento);
        }

}
