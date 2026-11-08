package modelo;

public class CuentaCorriente extends Cuenta {

    public CuentaCorriente(Usuario titular, double saldo) {
        super(titular, saldo);
    }

    // Regla del dominio: en corriente se puede retirar todo el saldo.
    @Override
    public double calcularSaldoDisponible() {
        return getSaldo();
    }
}