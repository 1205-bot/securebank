package modelo;

import java.time.LocalDateTime;

public class Transaccion {
    private static int contadorId = 1;

    private final int id;
    private final LocalDateTime fechaHora;
    private final TipoTransaccion tipo;
    private final double monto;
    private final int numeroCuentaOrigen;
    private final Integer numeroCuentaDestino; //con Integer para permitir nulos en caso de que no haya cuenta destino (por ejemplo, en un depósito o retiro)

    // Constructor para transferencias (origen y destino)
    public Transaccion(TipoTransaccion tipo, double monto, int numeroCuentaOrigen, Integer numeroCuentaDestino) {
        if(tipo == null) {
            throw new IllegalArgumentException("El tipo de transacción no puede ser nulo.");
        }
        if(monto <= 0) {
            throw new IllegalArgumentException("El monto de la transacción debe ser positivo.");
        }
        if(numeroCuentaOrigen <= 0) {
            throw new IllegalArgumentException("El número de cuenta de origen no puede ser cero o negativo.");
        }
        if(numeroCuentaDestino != null && numeroCuentaDestino <= 0) {
            throw new IllegalArgumentException("El número de cuenta de destino no puede ser cero o negativo.");
        }

        this.id = contadorId++;
        this.fechaHora = LocalDateTime.now();
        this.tipo = tipo;
        this.monto = monto;
        this.numeroCuentaOrigen = numeroCuentaOrigen;
        this.numeroCuentaDestino = numeroCuentaDestino;
    }

    // Sobrecarga de constructor para depósitos y retiros (solo una cuenta involucrada)
    public Transaccion(TipoTransaccion tipo, double monto, int numeroCuentaOrigen) {
        this(tipo, monto, numeroCuentaOrigen, null);
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public double getMonto() {
        return monto;
    }

    public int getNumeroCuentaOrigen() {
        return numeroCuentaOrigen;
    }

    public Integer getNumeroCuentaDestino() {
        return numeroCuentaDestino;
    }

    @Override
    public String toString() {
        String info = String.format("[%d] ID: %d | Tipo: %s |Monto: %.2f | Cuenta: %d", fechaHora, id, tipo, monto, numeroCuentaOrigen);
        if (numeroCuentaDestino != null) {
            info += " | Cuenta Destino: %d" + numeroCuentaDestino;
        }
        return info;
    }
}