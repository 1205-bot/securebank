# SecureBank

Proyecto Integrador de Programación Orientada a Objetos: **Sistema de Gestión de Acceso y Transacciones Bancarias**.

Desarrollado en **Java**.

## Equipo

| Integrante | Rol |
|---|---|
| Ramirez Olvera Erick Yael (@1205-bot) | Administrador del repositorio |
| Hanher-2025 | Desarrollador |
| achesanticar | Desarrollador |

## Estructura del proyecto

```
securebank/
├── src/
│   ├── app/        # Punto de entrada (Main.java)
│   ├── modelo/     # Clases del dominio (Usuario, Cuenta, Transacción, excepciones)
│   └── servicio/   # Lógica de negocio (gestor bancario)
├── .github/
│   └── pull_request_template.md
├── README.md
├── CONTRIBUTING.md
└── .gitignore
```

## Requisitos

- JDK 17 o superior
- Visual Studio Code con el *Extension Pack for Java*

## Cómo ejecutar

1. Clona el repositorio:
```bash
   git clone https://github.com/1205-bot/securebank.git
   cd securebank
```
2. Cambia a la rama de desarrollo:
```bash
   git checkout develop
```
3. Abre la carpeta en VS Code y ejecuta `Main.java` (paquete `app`) desde el botón *Run*, o desde la terminal:
```bash
   javac -d bin src/app/*.java src/modelo/*.java src/servicio/*.java
   java -cp bin app.Main
```

## Flujo de trabajo (resumen)

Nadie hace push directo a `main`, `staging` ni `develop`. Todo cambio entra mediante Pull Request.

```
feature/mi-tarea  →  develop  →  staging  →  main
```

Las reglas completas están en [CONTRIBUTING.md](CONTRIBUTING.md).