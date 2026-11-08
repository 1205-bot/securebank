package modelo;

import excepciones.SaldoInsuficienteException;

public class Cuenta {
    // Secuencial para que los casos de prueba sean reproducibles.
    private static int contadorNumero = 1;

    private final Usuario titular;
    private double saldo;
    private boolean bloqueada;
    private final int numeroCuenta; // Atributo único para cada cuenta

    // constructor de Cuenta
    public Cuenta(Usuario titular, double saldo) {
        // Validación de parámetros nulos
        if (titular == null) {
            throw new IllegalArgumentException("El titular no puede ser nulo");
        }
        if (saldo < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
 
        this.numeroCuenta = contadorNumero++;
        this.titular = titular;
        this.saldo = saldo;
        this.bloqueada = false;
    }

    // Método polimórfico: cada subclase define cuánto saldo puede retirarse.
    public double calcularSaldoDisponible() {
        return saldo;
    }

    // metodo para validar la operación antes de realizarla
    private void validarOperacion(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        if (this.bloqueada) {
            throw new IllegalStateException("La cuenta está bloqueada");
        }
    }

    // metodo para depositar dinero en la cuenta
    public void depositar(double monto) {
        validarOperacion(monto);
        this.saldo += monto;
    }   

    // metodo para retirar dinero de la cuenta
    public void retirar(double monto) throws SaldoInsuficienteException {
        validarOperacion(monto);
        double disponible = calcularSaldoDisponible();
        if (monto > disponible) {
            throw new SaldoInsuficienteException(String.format(
                    "saldo disponible %.2f, monto solicitado %.2f", disponible, monto));
        }
        this.saldo -= monto;
    }


    // gettes para acceder a los atributos privados
    public Usuario getTitular() {
        return titular;
    }

    public double getSaldo() {
        return saldo;
    }

    public boolean isBloqueada() {
        return bloqueada;
    }

    public int getNumeroCuenta() {
        return numeroCuenta;
    }
}