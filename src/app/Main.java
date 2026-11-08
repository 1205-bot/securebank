package app;

import java.util.List;
import java.util.Scanner;

import excepciones.SaldoInsuficienteException;
import modelo.Cuenta;
import modelo.RegistroAcceso;
import modelo.Rol;
import modelo.TipoCuenta;
import modelo.Transaccion;
import modelo.Usuario;
import servicio.AutenticacionService;
import servicio.BancoService;

public class Main {
    private static final Scanner TECLADO = new Scanner(System.in);

    public static void main(String[] args) {
        AutenticacionService auth = new AutenticacionService();
        BancoService banco = new BancoService();

        // Usuarios iniciales para reproducir los casos de prueba del README.
        auth.registrarUsuario(1, Rol.CLIENTE, "ana", "1234");
        auth.registrarUsuario(2, Rol.CLIENTE, "luis", "abcd");
        auth.registrarUsuario(3, Rol.CAJERO, "cajero1", "caja");
        auth.registrarUsuario(4, Rol.AUDITOR, "auditor1", "audit");
        auth.registrarUsuario(5, Rol.ADMINISTRADOR, "admin", "root");

        Usuario sesion = null;
        boolean salir = false;
        while (!salir) {
            System.out.println("\n=== BANCO === sesión: "
                    + (sesion == null ? "(ninguna)" : sesion.getNombreUsuario()));
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Registrar usuario");
            System.out.println("3. Crear cuenta");
            System.out.println("4. Consultar saldo");
            System.out.println("5. Depositar");
            System.out.println("6. Retirar");
            System.out.println("7. Transferir");
            System.out.println("8. Consultar historial");
            System.out.println("9. Consultar accesos");
            System.out.println("0. Salir");
            System.out.print("Opción: ");
            switch (TECLADO.nextLine().trim()) {
                case "1": sesion = iniciarSesion(auth); break;
                case "2": registrarUsuario(auth); break;
                case "3": crearCuenta(banco, sesion); break;
                case "4": consultarSaldo(banco, sesion); break;
                case "5": depositar(banco, sesion); break;
                case "6": retirar(banco, sesion); break;
                case "7": transferir(banco, sesion); break;
                case "8": consultarHistorial(banco, sesion); break;
                case "9": consultarAccesos(auth, sesion); break;
                case "0": salir = true; break;
                default: System.out.println("Opción no válida, elige un número del menú.");
            }
        }
        System.out.println("Sesión finalizada.");
    }

    private static Usuario iniciarSesion(AutenticacionService auth) {
        System.out.print("Usuario: ");
        String nombre = TECLADO.nextLine();
        System.out.print("Contraseña: ");
        String contrasena = TECLADO.nextLine();
        Usuario usuario = auth.autenticar(nombre, contrasena);
        if (usuario == null) {
            System.out.println("Acceso fallido: credenciales incorrectas o usuario bloqueado.");
        } else {
            System.out.println("Bienvenido, " + usuario.getNombreUsuario() + " (" + usuario.getRol() + ")");
        }
        return usuario;
    }

    private static void registrarUsuario(AutenticacionService auth) {
        try {
            System.out.print("Id (entero positivo): ");
            int id = Integer.parseInt(TECLADO.nextLine().trim());
            System.out.print("Rol (CLIENTE, CAJERO, AUDITOR, EJECUTIVO, ADMINISTRADOR): ");
            Rol rol = Rol.valueOf(TECLADO.nextLine().trim().toUpperCase());
            System.out.print("Nombre de usuario: ");
            String nombre = TECLADO.nextLine();
            System.out.print("Contraseña: ");
            String contrasena = TECLADO.nextLine();
            auth.registrarUsuario(id, rol, nombre, contrasena);
            System.out.println("Usuario registrado: " + nombre);
        } catch (NumberFormatException e) {
            System.out.println("Formato incorrecto: el id debe ser un número entero (ej. 6).");
        } catch (IllegalArgumentException e) {
            System.out.println("Datos rechazados: " + e.getMessage());
        }
    }

    private static void crearCuenta(BancoService banco, Usuario sesion) {
        try {
            System.out.print("Tipo (1 = AHORRO, 2 = CORRIENTE): ");
            int tipo = Integer.parseInt(TECLADO.nextLine().trim());
            System.out.print("Saldo inicial: ");
            double saldo = Double.parseDouble(TECLADO.nextLine().trim());
            if (tipo != 1 && tipo != 2) {
                throw new IllegalArgumentException("El tipo debe ser 1 o 2");
            }
            TipoCuenta tipoCuenta = (tipo == 1) ? TipoCuenta.AHORRO : TipoCuenta.CORRIENTE;
            Cuenta cuenta = banco.crearCuenta(sesion, tipoCuenta, saldo);
            System.out.println("Cuenta creada: " + cuenta.getNumeroCuenta()
                    + " (" + cuenta.getClass().getSimpleName() + ")");
        } catch (NumberFormatException e) {
            System.out.println("Formato incorrecto: usa números (tipo 1 o 2, saldo como 1000.00).");
        } catch (IllegalArgumentException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        }
    }

    // Polimorfismo observable: la referencia es Cuenta (tipo base) y se llama
    // al mismo método en ambas subclases; cada implementación da un resultado distinto.
    private static void consultarSaldo(BancoService banco, Usuario sesion) {
        System.out.print("Número de cuenta: ");
        String linea = TECLADO.nextLine();
        try {
            int numero = Integer.parseInt(linea.trim());
            double saldo = banco.consultarSaldo(sesion, numero);
            Cuenta cuenta = banco.buscarCuenta(numero);
            System.out.printf("Cuenta %d (%s) | saldo: %.2f | disponible para retiro: %.2f%n",
                    numero, cuenta.getClass().getSimpleName(), saldo, cuenta.calcularSaldoDisponible());
        } catch (NumberFormatException e) {
            System.out.println("Formato incorrecto: el número de cuenta es un entero (ej. 1).");
        } catch (IllegalArgumentException | SecurityException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        }
    }

    private static void depositar(BancoService banco, Usuario sesion) {
        System.out.print("Número de cuenta: ");
        String lineaCuenta = TECLADO.nextLine();
        System.out.print("Monto a depositar: ");
        String lineaMonto = TECLADO.nextLine();
        try {
            int numero = Integer.parseInt(lineaCuenta.trim());
            double monto = Double.parseDouble(lineaMonto.trim());
            banco.depositar(sesion, numero, monto);
            System.out.println("Depósito realizado. Nuevo saldo: " + banco.consultarSaldo(sesion, numero));
        } catch (NumberFormatException e) {
            System.out.println("Formato incorrecto: la cuenta es un entero y el monto un número (ej. 100.50).");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        }
    }

    private static void retirar(BancoService banco, Usuario sesion) {
        System.out.print("Número de cuenta: ");
        String lineaCuenta = TECLADO.nextLine();
        System.out.print("Monto a retirar: ");
        String lineaMonto = TECLADO.nextLine();
        try {
            int numero = Integer.parseInt(lineaCuenta.trim());
            double monto = Double.parseDouble(lineaMonto.trim());
            banco.retirar(sesion, numero, monto);
            // Confirmación de éxito: solo se imprime si la operación se completó.
            System.out.println("Retiro realizado. Nuevo saldo: " + banco.consultarSaldo(sesion, numero));
        } catch (NumberFormatException e) {
            System.out.println("Formato incorrecto: la cuenta es un entero y el monto un número (ej. 100.50).");
        } catch (SaldoInsuficienteException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        } finally {
            System.out.println("Intento de retiro finalizado.");
        }
    }

    private static void transferir(BancoService banco, Usuario sesion) {
        System.out.print("Cuenta origen: ");
        String lineaOrigen = TECLADO.nextLine();
        System.out.print("Cuenta destino: ");
        String lineaDestino = TECLADO.nextLine();
        System.out.print("Monto: ");
        String lineaMonto = TECLADO.nextLine();
        try {
            int origen = Integer.parseInt(lineaOrigen.trim());
            int destino = Integer.parseInt(lineaDestino.trim());
            double monto = Double.parseDouble(lineaMonto.trim());
            banco.transferir(sesion, origen, destino, monto);
            System.out.println("Transferencia realizada.");
        } catch (NumberFormatException e) {
            System.out.println("Formato incorrecto: las cuentas son enteros y el monto un número (ej. 100.50).");
        } catch (SaldoInsuficienteException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        }
    }

    private static void consultarHistorial(BancoService banco, Usuario sesion) {
        try {
            List<Transaccion> lista = banco.consultarHistorial(sesion);
            for (Transaccion t : lista) {
                System.out.println("   " + t);
            }
            System.out.println("Total: " + lista.size() + " transacciones");
        } catch (SecurityException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        }
    }

    private static void consultarAccesos(AutenticacionService auth, Usuario sesion) {
        try {
            List<RegistroAcceso> lista = auth.consultarAccesos(sesion);
            for (RegistroAcceso r : lista) {
                System.out.println("   " + r);
            }
            System.out.println("Total: " + lista.size() + " registros");
        } catch (SecurityException e) {
            System.out.println("Operación rechazada: " + e.getMessage());
        }
    }
}