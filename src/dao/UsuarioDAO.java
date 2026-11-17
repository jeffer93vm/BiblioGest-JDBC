package dao;

import conexion.ConexionBD;
import modelo.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    // 1. INSERCIÓN (CREATE)
    public boolean insertarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (documento, nombre_completo, correo, telefono) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, usuario.getDocumento());
            ps.setString(2, usuario.getNombreCompleto());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getTelefono());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    // 2. CONSULTA (READ)
    public List<Usuario> consultarUsuarios() {
        List<Usuario> listaUsuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setDocumento(rs.getString("documento"));
                usuario.setNombreCompleto(rs.getString("nombre_completo"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setTelefono(rs.getString("telefono"));
                listaUsuarios.add(usuario);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar usuarios: " + e.getMessage());
        }
        return listaUsuarios;
    }

    // 3. ACTUALIZACIÓN (UPDATE)
    public boolean actualizarTelefonoUsuario(String documento, String nuevoTelefono) {
        String sql = "UPDATE usuarios SET telefono = ? WHERE documento = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nuevoTelefono);
            ps.setString(2, documento);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar teléfono: " + e.getMessage());
            return false;
        }
    }

    // 4. ELIMINACIÓN (DELETE)
    public boolean eliminarUsuario(String documento) {
        String sql = "DELETE FROM usuarios WHERE documento = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, documento);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            return false;
        }
    }
}
