package modelo;

public class Usuario {
    private static final int Max_Intentos = 3;

    private final int id;
    private final Rol rol;
    private final String nombreUsuario;
    private String contrasena; // sin "final" pq puede cambiar 
    private int intentosFallidos;
    private boolean bloqueado; // bloqueado puede cambiar

    // constructor de Usuario
    public Usuario(int id, Rol rol, String nombreUsuario, String contrasena) {
        // Validación de parámetros nulos
        if (id <= 0 || rol == null || nombreUsuario == null || contrasena == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser nulos");
        }

        this.id = id;
        this.rol = rol;
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.intentosFallidos = 0;
        this.bloqueado = false;
    }

    // metodo q valida la contraseña del usuario y maneja los intentos fallidos
    public boolean validarContrasena(String contrasena) {
        if (this.bloqueado) {
            return false; // Usuario bloqueado, no se puede validar la contraseña
        }

        if (this.contrasena.equals(contrasena)) {
            this.intentosFallidos = 0; // Reiniciar intentos fallidos al ingresar correctamente
            return true;
        } else {
            this.intentosFallidos++;
            if (this.intentosFallidos >= Max_Intentos) {
                this.bloqueado = true; // Bloquear usuario después de 3 intentos fallidos
            }
            return false;
        }
    }

    // gettes para acceder a los atributos privados
    public int getId() {
        return id;
    }

    public Rol getRol() {
        return rol;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public int getIntentosFallidos() {
        return intentosFallidos;
    }

    public boolean isBloqueado() {
        return bloqueado;
    }
}