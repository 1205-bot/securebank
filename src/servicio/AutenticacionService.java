package servicio;

import java.util.ArrayList;
import java.util.List;

import modelo.RegistroAcceso;
import modelo.Rol;
import modelo.Usuario;

public class AutenticacionService {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<RegistroAcceso> registros = new ArrayList<>();

    // Registra un usuario nuevo con su rol. 
    public Usuario registrarUsuario(int id, Rol rol, String nombreUsuario, String contrasena) {
        for (Usuario u : usuarios) {
            if (u.getId() == id || u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                throw new IllegalArgumentException("Ya existe un usuario con ese id o nombre");
            }
        }
        Usuario nuevo = new Usuario(id, rol, nombreUsuario, contrasena);
        usuarios.add(nuevo);
        return nuevo;
    }

    // Autentica a un usuario.
    // Devuelve el usuario si entra, o null si falla. Siempre registra el intento.
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        String nombre = (nombreUsuario == null || nombreUsuario.isBlank()) ? "(vacío)" : nombreUsuario;
        Usuario usuario = buscarPorNombre(nombre);

        if (usuario == null) {
            registros.add(new RegistroAcceso(0, nombre, false, "Usuario no existe"));
            return null;
        }

        if (usuario.isBloqueado()) {
            registros.add(new RegistroAcceso(usuario.getId(), nombre, false, "Usuario bloqueado"));
            return null;
        }

        if (usuario.validarContrasena(contrasena)) {
            registros.add(new RegistroAcceso(usuario.getId(), nombre, true, "Credenciales correctas"));
            return usuario;
        }

        String detalle = "Contraseña incorrecta (intento " + usuario.getIntentosFallidos() + ")";
        if (usuario.isBloqueado()) {
            detalle += " - usuario bloqueado";
        }
        registros.add(new RegistroAcceso(usuario.getId(), nombre, false, detalle));
        return null;
    }

    // Solo administrador o auditor pueden ver los accesos
    public List<RegistroAcceso> consultarAccesos(Usuario solicitante) {
        if (solicitante == null || (solicitante.getRol() != Rol.ADMINISTRADOR && solicitante.getRol() != Rol.AUDITOR)) {
            throw new SecurityException("Solo administrador o auditor pueden ver los accesos");
        }
        return new ArrayList<>(registros);
    }

    private Usuario buscarPorNombre(String nombreUsuario) {
        for (Usuario u : usuarios) {
            // equalsIgnoreCase para que no importe mayúsculas/minúsculas
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return u;
            }
        }
        return null;
    }
}