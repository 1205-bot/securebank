import java.util.List;

import modelo.Cuenta;
import modelo.Rol;
import modelo.TipoCuenta;
import modelo.Transaccion;
import modelo.Usuario;
import servicio.BancoService;

public class Main {

    public static void main(String[] args) {
        BancoService banco = new BancoService();

        // ---------- Usuarios ----------
        Usuario ana = new Usuario(1, Rol.CLIENTE, "ana", "1234");
        Usuario luis = new Usuario(2, Rol.CLIENTE, "luis", "abcd");
        Usuario cajero = new Usuario(3, Rol.CAJERO, "cajero1", "caja");
        Usuario auditor = new Usuario(4, Rol.AUDITOR, "auditor1", "audit");
        Usuario admin = new Usuario(5, Rol.ADMINISTRADOR, "admin", "root");

        // ---------- 1. Crear cuentas ----------
        titulo("1. Crear cuentas");
        Cuenta cuentaAna = banco.crearCuenta(ana, TipoCuenta.AHORRO, 1000);
        Cuenta cuentaLuis = banco.crearCuenta(luis, TipoCuenta.CORRIENTE, 500);
        System.out.println("Cuenta de Ana (AHORRO): " + cuentaAna.getNumeroCuenta());
        System.out.println("Cuenta de Luis (CORRIENTE): " + cuentaLuis.getNumeroCuenta());

        prueba("Crear cuenta con titular que NO es CLIENTE (debe fallar)",
                () -> banco.crearCuenta(cajero, TipoCuenta.AHORRO, 100));
        prueba("Crear cuenta con saldo negativo (debe fallar)",
                () -> banco.crearCuenta(ana, TipoCuenta.AHORRO, -50));

        // ---------- 2. Depositar ----------
        titulo("2. Depositar");
        prueba("Ana deposita 500 en su cuenta",
                () -> banco.depositar(ana, cuentaAna.getNumeroCuenta(), 500));
        System.out.println("Saldo de Ana: " + banco.consultarSaldo(ana, cuentaAna.getNumeroCuenta())); // 1500

        prueba("Depositar monto 0 (debe fallar)",
                () -> banco.depositar(ana, cuentaAna.getNumeroCuenta(), 0));

        // ---------- 3. Retirar ----------
        titulo("3. Retirar");
        prueba("Luis retira 200 (corriente)",
                () -> banco.retirar(luis, cuentaLuis.getNumeroCuenta(), 200));
        System.out.println("Saldo de Luis: " + banco.consultarSaldo(luis, cuentaLuis.getNumeroCuenta())); // 300

        prueba("Luis retira 1000 (saldo insuficiente, debe fallar)",
                () -> banco.retirar(luis, cuentaLuis.getNumeroCuenta(), 1000));

        // Cuenta de ahorro: solo puede retirar el 90% (de 1500 -> 1350)
        prueba("Ana retira 1400 de AHORRO (excede el 90%, debe fallar)",
                () -> banco.retirar(ana, cuentaAna.getNumeroCuenta(), 1400));
        prueba("Ana retira 1350 de AHORRO (justo el 90%, debe funcionar)",
                () -> banco.retirar(ana, cuentaAna.getNumeroCuenta(), 1350));
        System.out.println("Saldo de Ana: " + banco.consultarSaldo(ana, cuentaAna.getNumeroCuenta())); // 150

        // ---------- 4. Transferencias ----------
        titulo("4. Transferencias");
        prueba("Luis transfiere 100 a Ana",
                () -> banco.transferir(luis, cuentaLuis.getNumeroCuenta(), cuentaAna.getNumeroCuenta(), 100));
        System.out.println("Saldo de Luis: " + banco.consultarSaldo(luis, cuentaLuis.getNumeroCuenta())); // 200
        System.out.println("Saldo de Ana: " + banco.consultarSaldo(ana, cuentaAna.getNumeroCuenta())); // 250

        prueba("Transferir a la misma cuenta (debe fallar)",
                () -> banco.transferir(luis, cuentaLuis.getNumeroCuenta(), cuentaLuis.getNumeroCuenta(), 10));
        prueba("Transferir a cuenta inexistente (debe fallar)",
                () -> banco.transferir(luis, cuentaLuis.getNumeroCuenta(), 999999999, 10));
        prueba("Transferir mas de lo que hay (debe fallar)",
                () -> banco.transferir(luis, cuentaLuis.getNumeroCuenta(), cuentaAna.getNumeroCuenta(), 5000));
        System.out.println("Saldo de Luis (no debio cambiar): "
                + banco.consultarSaldo(luis, cuentaLuis.getNumeroCuenta())); // 200

        // ---------- 5. Permisos ----------
        titulo("5. Permisos");
        prueba("Luis consulta el saldo de Ana (debe fallar)",
                () -> banco.consultarSaldo(luis, cuentaAna.getNumeroCuenta()));
        prueba("Cajero consulta el saldo de Ana (debe funcionar)",
                () -> System.out.println("   Saldo: " + banco.consultarSaldo(cajero, cuentaAna.getNumeroCuenta())));
        prueba("Sin sesion (null) consulta saldo (debe fallar)",
                () -> banco.consultarSaldo(null, cuentaAna.getNumeroCuenta()));

        // ---------- 6. Historial ----------
        titulo("6. Historial");
        prueba("Cliente intenta ver el historial (debe fallar)",
                () -> banco.consultarHistorial(ana));
        prueba("Cajero intenta ver el historial (debe fallar)",
                () -> banco.consultarHistorial(cajero));

        System.out.println("Historial visto por el AUDITOR:");
        imprimirHistorial(banco.consultarHistorial(auditor));
        System.out.println("Historial visto por el ADMINISTRADOR: "
                + banco.consultarHistorial(admin).size() + " transacciones");

        // ---------- 7. Bloqueo de usuario ----------
        titulo("7. Bloqueo de usuario (3 intentos fallidos)");
        for (int i = 1; i <= 4; i++) {
            boolean ok = luis.validarContrasena("incorrecta");
            System.out.println("Intento " + i + " -> acceso: " + ok
                    + " | fallidos: " + luis.getIntentosFallidos()
                    + " | bloqueado: " + luis.isBloqueado());
        }
        System.out.println("Luis con su contrasena correcta estando bloqueado: "
                + luis.validarContrasena("abcd")); // false
    }

    // ---------- Utilidades ----------

    private static void titulo(String texto) {
        System.out.println("\n=== " + texto + " ===");
    }

    // Ejecuta una operacion y muestra si funciono o que error lanzo
    private static void prueba(String descripcion, Runnable operacion) {
        try {
            operacion.run();
            System.out.println("[OK]    " + descripcion);
        } catch (RuntimeException e) {
            System.out.println("[ERROR] " + descripcion + " -> "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    // Se imprime con getters para no depender de Transaccion.toString()
    private static void imprimirHistorial(List<Transaccion> lista) {
        for (Transaccion t : lista) {
            String destino = (t.getNumeroCuentaDestino() != null)
                    ? " -> " + t.getNumeroCuentaDestino() : "";
            System.out.println("   #" + t.getId() + " | " + t.getFechaHora()
                    + " | " + t.getTipo() + " | $" + t.getMonto()
                    + " | cuenta " + t.getNumeroCuentaOrigen() + destino);
        }
    }
}