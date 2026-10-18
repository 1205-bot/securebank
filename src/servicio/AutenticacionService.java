package servicio;

import java.util.ArrayList;
import java.util.List;

import modelo.RegistroAcceso;
import modelo.Rol;
import modelo.Usuario;

public class AutenticacionService {
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<RegistroAcceso> registros = new ArrayList<>();

    public Usuario registrarUsuario(int id, Rol rol, String nombreUsuario, String contrasena) {
        for (Usuario u : usuarios) {
            // Verifica si ya existe un usuario con el mismo id o nombre de usuario
            if (u.getId() == id || u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                throw new IllegalArgumentException("Ya existe un usuario con ese id o nombre");
            }
        }

        Usuario nuevo = new Usuario(id, rol, nombreUsuario, contrasena);
        usuarios.add(nuevo);
        return nuevo;
    }

    // Devuelve el usuario si las credenciales son correctas, o null si no.
    // Siempre deja un registro del intento (exitoso o fallido).
    public Usuario autenticar(String nombreUsuario, String contrasena) {
        Usuario usuario = buscarPorNombre(nombreUsuario);
        boolean exitoso = usuario != null && usuario.validarContrasena(contrasena);
        registros.add(new RegistroAcceso(nombreUsuario, exitoso));
        return exitoso ? usuario : null;
    }

    public List<RegistroAcceso> consultarAccesos(Usuario solicitante) {
        if (solicitante == null || (solicitante.getRol() != Rol.ADMINISTRADOR && solicitante.getRol() != Rol.AUDITOR)) {
            throw new SecurityException("Solo administrador o auditor pueden ver los accesos");
        }
        return new ArrayList<>(registros);
    }

    private Usuario buscarPorNombre(String nombreUsuario) {
        for (Usuario u : usuarios) {
            if (u.getNombreUsuario().equalsIgnoreCase(nombreUsuario)) {
                return u;
            }
        }
        return null;
    }
}