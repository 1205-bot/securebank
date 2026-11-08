package modelo;

public class CuentaAhorro extends Cuenta {
    private static final double PORCENTAJE_RETIRO = 0.9;

    public CuentaAhorro(Usuario titular, double saldo) {
        super(titular, saldo);
    }

    // Regla del dominio: en ahorro solo se retira hasta el 90% del saldo.
    @Override
    public double calcularSaldoDisponible() {
        return getSaldo() * PORCENTAJE_RETIRO;
    }
}