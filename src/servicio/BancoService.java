package servicio;

import java.util.ArrayList;
import java.util.List;

import modelo.Cuenta;
import modelo.Rol;
import modelo.TipoCuenta;
import modelo.TipoTransaccion;
import modelo.Transaccion;
import modelo.Usuario;

public class BancoService {
    private final List<Cuenta> cuentas = new ArrayList<>();
    private final List<Transaccion> transacciones = new ArrayList<>();

    public Cuenta crearCuenta(Usuario titular, TipoCuenta tipo, double saldoInicial) {
        if (titular == null || titular.getRol() != Rol.CLIENTE) {
            throw new IllegalArgumentException("El titular debe ser un usuario con rol CLIENTE");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        Cuenta cuenta;
        do {
            cuenta = new Cuenta(tipo, titular, saldoInicial); // se repite si el número aleatorio ya existe, Math.random()
        } while (buscarCuenta(cuenta.getNumeroCuenta()) != null); // Asegura que el número de cuenta sea único
        cuentas.add(cuenta);
        return cuenta;
    }

    // metodo para buscar una cuenta por su número, devuelve null si no existe
    public Cuenta buscarCuenta(int numero) {
        for (Cuenta c : cuentas) {
            if (c.getNumeroCuenta() == numero) {
                return c;
            }
        }
        return null;
    }

    // metodo para consultar el saldo de una cuenta, solo el titular o un cajero pueden hacerlo
    public double consultarSaldo(Usuario solicitante, int numeroCuenta) {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);
        validarPermiso(solicitante, cuenta);
        return cuenta.getSaldo();
    }

    // metodo para depositar dinero en una cuenta, solo el titular o un cajero pueden hacerlo
    public void depositar(Usuario solicitante, int numeroCuenta, double monto) {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);
        validarPermiso(solicitante, cuenta);
        cuenta.depositar(monto);
        // Depósito: solo hay una cuenta involucrada
        transacciones.add(new Transaccion(TipoTransaccion.DEPOSITO, monto, cuenta.getNumeroCuenta()));
    }

    // metodo para retirar dinero de una cuenta, solo el titular o un cajero pueden hacerlo
    public void retirar(Usuario solicitante, int numeroCuenta, double monto) {
        Cuenta cuenta = obtenerCuenta(numeroCuenta);
        validarPermiso(solicitante, cuenta);
        cuenta.retirar(monto);
        transacciones.add(new Transaccion(TipoTransaccion.RETIRO, monto, cuenta.getNumeroCuenta()));
    }

    // metodo para transferir dinero entre cuentas, solo el titular de la cuenta origen o un cajero pueden hacerlo
    public void transferir(Usuario solicitante, int numeroOrigen, int numeroDestino, double monto) {
        if (numeroOrigen == numeroDestino) {
            throw new IllegalArgumentException("La cuenta origen y destino deben ser distintas");
        }
        Cuenta origen = obtenerCuenta(numeroOrigen);
        Cuenta destino = obtenerCuenta(numeroDestino);
        validarPermiso(solicitante, origen); // solo el dueño de la cuenta origen (o un cajero)

        origen.retirar(monto);

    try {
        destino.depositar(monto);      // 1. intenta; aquí dentro hay un "throw" si algo falla
    } catch (RuntimeException e) {     // 2. si hubo throw, llega aquí
        origen.depositar(monto);       // 3. arreglas el problema (devuelves el dinero)
        throw e;                       // 4. avisas a quien llamó que falló
    }

        transacciones.add(new Transaccion(TipoTransaccion.TRANSFERENCIA, monto, origen.getNumeroCuenta(), destino.getNumeroCuenta()));
    }

    public List<Transaccion> consultarHistorial(Usuario solicitante) {
        if (solicitante == null || (solicitante.getRol() != Rol.ADMINISTRADOR && solicitante.getRol() != Rol.AUDITOR)) {
            throw new SecurityException("Solo administrador o auditor pueden ver el historial");
        }
        return new ArrayList<>(transacciones); // copia, para no exponer la lista interna
    }

    private Cuenta obtenerCuenta(int numero) {
        Cuenta cuenta = buscarCuenta(numero);
        if (cuenta == null) {
            throw new IllegalArgumentException("No existe la cuenta " + numero);
        }
        return cuenta;
    }

    // Puede operar el titular de la cuenta o un cajero
    private void validarPermiso(Usuario solicitante, Cuenta cuenta) {
        if (solicitante == null) {
            throw new SecurityException("Debes iniciar sesión");
        }
        boolean esTitular = cuenta.getTitular().getId() == solicitante.getId();
        boolean esCajero = solicitante.getRol() == Rol.CAJERO;
        if (!esTitular && !esCajero) {
            throw new SecurityException("No tienes permiso para operar esta cuenta");
        }
    }
}