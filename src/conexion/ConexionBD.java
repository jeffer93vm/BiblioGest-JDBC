package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    
    private static final String URL = "jdbc:mysql://localhost:3306/bibliogest";
    private static final String USUARIO = "root";
    private static final String CONTRASENA = "3133895366";

    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            System.out.println("-> Conexión exitosa a la base de datos BiblioGest.");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error de conexión JDBC: " + e.getMessage());
        }
        return conexion;
    }
}
