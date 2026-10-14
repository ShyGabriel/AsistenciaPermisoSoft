# AsistenciaPermisoSoft

Sistema de control de asistencia y permisos. Backend en Spring Boot y frontend en Angular con autenticación por JWT y dos roles: `ADMIN` y `EMPLEADO`.

## Estructura

- `principal/` — API REST (Spring Boot 4, Java 21).
- `frontend/` — Aplicación Angular (standalone, CSS puro).
- `principal/db/` — Script de creación de la base de datos.

## Requisitos

- Java 21+
- Node.js 20+ y npm
- Maven (o usar el wrapper `mvnw` incluido)
- MySQL 8/9 en ejecución

## Base de datos (MySQL)

El backend usa la base de datos `gestion_empleados`. El script crea el esquema completo y los datos de prueba:

```bash
cd principal
mysql -u root --default-character-set=utf8mb4 < db/gestion_empleados.sql
```

> En Windows, si el cliente no está en el PATH:
> `"C:\Program Files\MySQL\MySQL Server 9.7\bin\mysql.exe" -u root --default-character-set=utf8mb4 < db/gestion_empleados.sql`

Configuración por defecto (se puede sobrescribir con variables de entorno):

| Propiedad | Variable | Valor por defecto |
| --------- | -------- | ----------------- |
| URL       | `DB_URL` | `jdbc:mysql://localhost:3306/gestion_empleados` |
| Usuario   | `DB_USER` | `root` |
| Contraseña| `DB_PASSWORD` | *(vacío)* |

## Backend

```bash
cd principal
./mvnw spring-boot:run
```

Se levanta en `http://localhost:8080` y autentica contra MySQL (los usuarios viven en la tabla `Usuario`).

### Endpoint de login

`POST /api/auth/login`

```json
{ "email": "admin@empresa.com", "password": "Admin123!" }
```

Respuesta:

```json
{ "token": "<jwt>", "email": "admin@empresa.com", "rol": "ADMIN", "nombre": "Administrador Sistema" }
```

### Usuarios de prueba

| Email                       | Contraseña    | Rol      |
| --------------------------- | ------------- | -------- |
| admin@empresa.com           | Admin123!     | ADMIN    |
| maria.lopez@empresa.com     | Admin123!     | ADMIN    |
| juan.perez@empresa.com      | Empleado123!  | EMPLEADO |
| carlos.martinez@empresa.com | Empleado123!  | EMPLEADO |

> El secreto JWT se configura con la variable de entorno `JWT_SECRET` (hay un valor por defecto para desarrollo).

## Frontend

```bash
cd frontend
npm install
npm start
```

Se levanta en `http://localhost:4200`. El proxy de desarrollo (`proxy.conf.json`) redirige `/api` a `http://localhost:8080`.

### Rutas

- `/login` — inicio de sesión.
- `/dashboard/admin` — panel del administrador (solo ADMIN).
- `/dashboard/empleado` — panel del empleado (solo EMPLEADO).

Tras iniciar sesión, cada usuario es redirigido a su dashboard según el rol. La sesión se guarda en `localStorage` y los guards validan autenticación y expiración del token.

## Tests

```bash
cd principal
./mvnw test
```
