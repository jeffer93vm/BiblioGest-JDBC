package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import dao.LibroDAO;
import dao.LoginDAO;
import dao.UsuarioDAO;
import modelo.Libro;
import modelo.Usuario;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class ServidorWeb {

    private static final LibroDAO libroDao = new LibroDAO();
    private static final UsuarioDAO usuarioDao = new UsuarioDAO();
    private static final LoginDAO loginDao = new LoginDAO();
    private static boolean sesionIniciada = false;

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                if (sesionIniciada) {
                    enviarHTML(exchange, cargarPanel());
                } else {
                    enviarHTML(exchange, Files.readString(Path.of("public/login.html"), StandardCharsets.UTF_8));
                }
            }
        });

        server.createContext("/login", exchange -> {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> datos = leerFormulario(exchange);
                if (loginDao.validarAcceso(datos.get("usuario"), datos.get("clave"))) {
                    sesionIniciada = true;
                }
                redirigir(exchange, "/");
            }
        });

        server.createContext("/logout", exchange -> {
            sesionIniciada = false;
            redirigir(exchange, "/");
        });

        server.createContext("/libros/agregar", exchange -> {
            if ("POST".equals(exchange.getRequestMethod()) && sesionIniciada) {
                Map<String, String> d = leerFormulario(exchange);
                libroDao.insertarLibro(new Libro(d.get("isbn"), d.get("titulo"), d.get("autor"), Integer.parseInt(d.get("cantidad"))));
                redirigir(exchange, "/");
            }
        });

        server.createContext("/libros/actualizar", exchange -> {
            if ("POST".equals(exchange.getRequestMethod()) && sesionIniciada) {
                Map<String, String> d = leerFormulario(exchange);
                libroDao.actualizarStockLibro(d.get("isbn"), Integer.parseInt(d.get("cantidad")));
                redirigir(exchange, "/");
            }
        });

        server.createContext("/libros/eliminar", exchange -> {
            if ("POST".equals(exchange.getRequestMethod()) && sesionIniciada) {
                Map<String, String> d = leerFormulario(exchange);
                libroDao.eliminarLibro(d.get("isbn"));
                redirigir(exchange, "/");
            }
        });

        server.createContext("/usuarios/agregar", exchange -> {
            if ("POST".equals(exchange.getRequestMethod()) && sesionIniciada) {
                Map<String, String> d = leerFormulario(exchange);
                usuarioDao.insertarUsuario(new Usuario(d.get("documento"), d.get("nombre"), d.get("correo"), d.get("telefono")));
                redirigir(exchange, "/");
            }
        });

        server.createContext("/usuarios/actualizar", exchange -> {
            if ("POST".equals(exchange.getRequestMethod()) && sesionIniciada) {
                Map<String, String> d = leerFormulario(exchange);
                usuarioDao.actualizarTelefonoUsuario(d.get("documento"), d.get("telefono"));
                redirigir(exchange, "/");
            }
        });

        server.createContext("/usuarios/eliminar", exchange -> {
            if ("POST".equals(exchange.getRequestMethod()) && sesionIniciada) {
                Map<String, String> d = leerFormulario(exchange);
                usuarioDao.eliminarUsuario(d.get("documento"));
                redirigir(exchange, "/");
            }
        });

        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("SERVIDOR LISTO EN: http://localhost:8080");
        try { Thread.currentThread().join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private static String cargarPanel() throws IOException {
        String plantilla = Files.readString(Path.of("public/panel.html"), StandardCharsets.UTF_8);
        StringBuilder filasLibros = new StringBuilder();
        for (Libro l : libroDao.consultarLibros()) {
            filasLibros.append(String.format(
                "<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td>" +
                "<td><form action='/libros/actualizar' method='POST' style='display:flex;gap:5px;'>" +
                "<input type='hidden' name='isbn' value='%s'>" +
                "<input type='number' name='cantidad' value='%d' style='width:60px;'>" +
                "<button type='submit' class='btn-upd'>Actualizar</button></form></td>" +
                "<td><form action='/libros/eliminar' method='POST'>" +
                "<input type='hidden' name='isbn' value='%s'>" +
                "<button type='submit' class='btn-del'>Eliminar</button></form></td></tr>",
                l.getIdLibro(), l.getIsbn(), l.getTitulo(), l.getAutor(), l.getIsbn(), l.getCantidadDisponible(), l.getIsbn()
            ));
        }

        StringBuilder filasUsuarios = new StringBuilder();
        for (Usuario u : usuarioDao.consultarUsuarios()) {
            filasUsuarios.append(String.format(
                "<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td>" +
                "<td><form action='/usuarios/actualizar' method='POST' style='display:flex;gap:5px;'>" +
                "<input type='hidden' name='documento' value='%s'>" +
                "<input type='text' name='telefono' value='%s' style='width:110px;'>" +
                "<button type='submit' class='btn-upd'>Actualizar</button></form></td>" +
                "<td><form action='/usuarios/eliminar' method='POST'>" +
                "<input type='hidden' name='documento' value='%s'>" +
                "<button type='submit' class='btn-del'>Eliminar</button></form></td></tr>",
                u.getIdUsuario(), u.getDocumento(), u.getNombreCompleto(), u.getCorreo(), u.getDocumento(), u.getTelefono(), u.getDocumento()
            ));
        }

        return plantilla.replace("<!-- FILAS_LIBROS -->", filasLibros.toString())
                        .replace("<!-- FILAS_USUARIOS -->", filasUsuarios.toString());
    }

    private static Map<String, String> leerFormulario(HttpExchange exchange) throws IOException {
        String cuerpo = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, String> mapa = new HashMap<>();
        for (String par : cuerpo.split("&")) {
            int pos = par.indexOf("=");
            if (pos > 0) {
                mapa.put(URLDecoder.decode(par.substring(0, pos), StandardCharsets.UTF_8),
                         URLDecoder.decode(par.substring(pos + 1), StandardCharsets.UTF_8));
            }
        }
        return mapa;
    }

    private static void enviarHTML(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }

    private static void redirigir(HttpExchange exchange, String ruta) throws IOException {
        exchange.getResponseHeaders().set("Location", ruta);
        exchange.sendResponseHeaders(302, -1);
    }
}
