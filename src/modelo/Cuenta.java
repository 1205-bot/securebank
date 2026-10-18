package modelo;

public class Cuenta {
    private final TipoCuenta tipoCuenta;
    private final Usuario titular;
    private double saldo;
    private boolean bloqueada;
    private final int numeroCuenta; // Atributo único para cada cuenta

    // constructor de Cuenta
    public Cuenta(TipoCuenta tipoCuenta, Usuario titular, double saldo) {
        // Validación de parámetros nulos
        if (tipoCuenta == null || titular == null) {
            throw new IllegalArgumentException("Los parámetros no pueden ser nulos");
        }

        this.numeroCuenta = (int) (Math.random() * 1000000); // Genera un número de cuenta aleatorio
        this.tipoCuenta = tipoCuenta;
        this.titular = titular;
        this.saldo = saldo;
        this.bloqueada = false;
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
    public void retirar(double monto) {
        validarOperacion(monto);
        
        double saldoDisponible = this.saldo;
        if (this.tipoCuenta == TipoCuenta.AHORRO) {
            saldoDisponible = this.saldo * 0.9; // Los clientes solo pueden retirar hasta el 90% de su saldo
        }
        if (monto > saldoDisponible) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        this.saldo -= monto;
    }


    // gettes para acceder a los atributos privados
    public TipoCuenta getTipoCuenta() {
        return tipoCuenta;
    }

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