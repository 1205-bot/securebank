# Guía de contribución

Reglas para trabajar en equipo en este repositorio.

## Ramas

| Rama | Propósito | Push directo |
|---|---|---|
| `main` | Versión estable, lista para entregar | No |
| `staging` | Zona de pruebas (QA) antes de pasar a `main` | No |
| `develop` | Integración del trabajo de todos | No |
| `feature/<nombre>` | Una tarea concreta | Sí (es tu rama) |

Ejemplos de nombres: `feature/clase-cuenta`, `feature/login-usuario`, `feature/validar-transaccion`.

Otros prefijos útiles: `fix/<nombre>` para corregir errores y `docs/<nombre>` para documentación.

## Flujo de trabajo

1. Actualiza `develop`:
```bash
   git checkout develop
   git pull
```
2. Crea tu rama desde `develop`:
```bash
   git checkout -b feature/mi-tarea
```
3. Programa y haz commits pequeños y frecuentes.
4. Sube tu rama:
```bash
   git push -u origin feature/mi-tarea
```
5. En GitHub, abre un **Pull Request** de `feature/mi-tarea` hacia `develop`.
6. Otro integrante revisa y aprueba (mínimo **1 aprobación**).
7. Se hace *Merge* y se puede borrar la rama de la tarea.
8. Cuando `develop` esté estable, se abre un PR de `develop` hacia `staging` para probar todo junto.
9. Si `staging` funciona bien, el administrador abre y aprueba el PR de `staging` hacia `main`.

## Mensajes de commit

Formato: `tipo: descripción corta en presente`

| Tipo | Uso |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección de un error |
| `docs` | Cambios en documentación |
| `refactor` | Reorganizar código sin cambiar su comportamiento |
| `test` | Agregar o modificar pruebas |
| `chore` | Tareas de mantenimiento |

Ejemplos:
- `feat: agrega clase Cuenta con saldo y titular`
- `fix: corrige validación de monto negativo`
- `docs: actualiza README con instrucciones`

## Pull Requests

- Un PR = una tarea. Mientras más pequeño, más fácil de revisar.
- Llena la plantilla del PR (qué hiciste y cómo probarlo).
- Resuelve todos los comentarios de la revisión antes de hacer merge.
- Si cambias código después de que te aprobaron, hay que volver a pedir aprobación.
- Nadie aprueba su propio PR.

## Conflictos

Si tu PR muestra conflictos con `develop`:

```bash
git checkout feature/mi-tarea
git pull origin develop
# resuelve los conflictos en los archivos marcados
git add .
git commit -m "fix: resuelve conflictos con develop"
git push
```

## Estilo de código Java

- Clases en `PascalCase` (`CuentaBancaria`), métodos y variables en `camelCase` (`calcularSaldo`).
- Constantes en `MAYUSCULAS_CON_GUION_BAJO`.
- Un archivo por clase, dentro de su paquete correspondiente.
- Atributos `private` con getters y setters cuando haga falta (encapsulamiento).
- Comenta lo que no sea evidente, no cada línea.