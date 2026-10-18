package modelo;

import java.time.LocalDateTime;

public class RegistroAcceso {
    private static int contadorId = 1;

    private final int id;
    private final int idUsuario;
    private final String nombreUsuario;
    private final LocalDateTime fechaHora;
    private final boolean exitoso;
    private final String detalle;

    public RegistroAcceso(int idUsuario, String nombreUsuario, boolean exitoso, String detalle) {
        if (nombreUsuario == null || nombreUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }

        this.id = contadorId++;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.fechaHora = LocalDateTime.now();
        this.exitoso = exitoso;
        this.detalle = (detalle != null) ? detalle : "";
    }

    public int getId() {
        return id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public boolean isExitoso() {
        return exitoso;
    }

    public String getDetalle() {
        return detalle;
    }

    @Override
    public String toString() {
        return String.format("[%s] Acceso ID %d | Usuario: %s (ID: %d) | Estado: %s | Detalle: %s", fechaHora, id, nombreUsuario, idUsuario, exitoso ? "Exitoso" : "Fallido", detalle);
    }
}