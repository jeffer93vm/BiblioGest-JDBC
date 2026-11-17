package dao;

import conexion.ConexionBD;
import modelo.Libro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    public boolean insertarLibro(Libro libro) {
        String sql = "INSERT INTO libros (isbn, titulo, autor, cantidad_disponible) VALUES (?, ?, ?, ?)";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, libro.getIsbn());
            ps.setString(2, libro.getTitulo());
            ps.setString(3, libro.getAutor());
            ps.setInt(4, libro.getCantidadDisponible());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al insertar libro: " + e.getMessage());
            return false;
        }
    }

    public List<Libro> consultarLibros() {
        List<Libro> listaLibros = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setIsbn(rs.getString("isbn"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setAutor(rs.getString("autor"));
                libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                listaLibros.add(libro);
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar libros: " + e.getMessage());
        }
        return listaLibros;
    }

    public boolean actualizarStockLibro(String isbn, int nuevaCantidad) {
        String sql = "UPDATE libros SET cantidad_disponible = ? WHERE isbn = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, nuevaCantidad);
            ps.setString(2, isbn);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarLibro(String isbn) {
        String sql = "DELETE FROM libros WHERE isbn = ?";
        try (Connection con = ConexionBD.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, isbn);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }
}
