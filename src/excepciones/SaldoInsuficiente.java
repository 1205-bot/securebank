package excepciones;

// Excepción propia verificada: retirar más de lo disponible en la cuenta.
public class SaldoInsuficienteException extends Exception {

    public SaldoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}