package vista;

import dao.LibroDAO;
import dao.UsuarioDAO;
import modelo.Libro;
import modelo.Usuario;
import java.util.List;

public class Principal {
    public static void main(String[] args) {
        
        // ==========================================
        // MÓDULO 1: GESTIÓN DE LIBROS
        // ==========================================
        System.out.println("==========================================");
        System.out.println("       MÓDULO 1: GESTIÓN DE LIBROS        ");
        System.out.println("==========================================");
        LibroDAO libroDao = new LibroDAO();

        Libro nuevoLibro = new Libro("978-0307474728", "Cien años de soledad", "Gabriel García Márquez", 10);
        if (libroDao.insertarLibro(nuevoLibro)) {
            System.out.println("[OK] Libro registrado con éxito.");
        }

        List<Libro> inventario = libroDao.consultarLibros();
        for (Libro l : inventario) {
            System.out.println(" -> ID: " + l.getIdLibro() + " | Título: " + l.getTitulo() + " | Stock: " + l.getCantidadDisponible());
        }

        if (libroDao.actualizarStockLibro("978-0307474728", 8)) {
            System.out.println("[OK] Stock de libro actualizado correctamente.");
        }

        if (libroDao.eliminarLibro("978-0307474728")) {
            System.out.println("[OK] Registro de libro de prueba eliminado.");
        }

        // ==========================================
        // MÓDULO 2: GESTIÓN DE USUARIOS (LECTORES)
        // ==========================================
        System.out.println("\n==========================================");
        System.out.println("      MÓDULO 2: GESTIÓN DE USUARIOS       ");
        System.out.println("==========================================");
        UsuarioDAO usuarioDao = new UsuarioDAO();

        Usuario nuevoUsuario = new Usuario("1015432890", "Laura Gómez", "laura.gomez@correo.com", "3104567890");
        if (usuarioDao.insertarUsuario(nuevoUsuario)) {
            System.out.println("[OK] Usuario lector registrado con éxito.");
        }

        List<Usuario> listaUsuarios = usuarioDao.consultarUsuarios();
        for (Usuario u : listaUsuarios) {
            System.out.println(" -> ID: " + u.getIdUsuario() + " | Nombre: " + u.getNombreCompleto() + " | Tel: " + u.getTelefono());
        }

        if (usuarioDao.actualizarTelefonoUsuario("1015432890", "3209876543")) {
            System.out.println("[OK] Teléfono de usuario actualizado correctamente.");
        }

        if (usuarioDao.eliminarUsuario("1015432890")) {
            System.out.println("[OK] Registro de usuario de prueba eliminado.");
        }
    }
}
