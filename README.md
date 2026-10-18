# Demo1

Aplicación web con frontend servido por Nginx, backend Spring Boot y PostgreSQL.

## Estructura

```text
demo1/
├── services/
│   └── backend/
│       ├── src/
│       ├── pom.xml
│       └── Dockerfile
├── frontend/
├── docker-compose.yml
└── README.md
```

## Ejecución

Desde esta carpeta:

```powershell
docker compose up --build -d
```

Abrir <http://localhost:8080>. El endpoint del backend está disponible en
<http://localhost:8080/api/hello>.

Los datos de PostgreSQL se conservan en el volumen Docker `demo1-postgres-data`.
Para Compose se usa una clave JWT local de desarrollo. Puedes reemplazarla antes
de iniciar los contenedores estableciendo `JWT_SECRET` en tu entorno; no uses la
clave de desarrollo en Render ni en otro entorno compartido.

## API de inventario y autenticación

Las rutas de negocio requieren un JWT, excepto el inicio de sesión. Envía el token
en la cabecera `Authorization: Bearer <token>`.

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/api/auth/login` | Público | Verifica credenciales y devuelve `{ "token", "role" }`. |
| `POST` | `/api/auth/logout` | Usuario autenticado | Añade el token actual a la lista de revocación en memoria. |
| `GET` | `/api/inventory/{storeId}` | `ADMIN`, `BODEGUERO` | Lista `productId`, `productName` y `cantidad`. |
| `GET` | `/api/inventory/{storeId}/low-stock?threshold=10` | `ADMIN`, `BODEGUERO` | Lista productos con `cantidad <= threshold`; el umbral predeterminado es `10`. |
| `POST` | `/api/sales` | `ADMIN`, `VENDEDOR` | Registra una venta. |

Los usuarios con rol `ADMIN` pueden acceder a todas las rutas. El resto de rutas
de negocio quedan restringidas a `ADMIN`.

Ejemplo de inicio de sesión:

```json
{
  "username": "admin",
  "password": "tu-contraseña"
}
```

### Preparar usuarios en PostgreSQL/Neon

No hace falta crear manualmente la tabla `users`: al conectarse a la base de
datos, Hibernate aplica las columnas definidas por `UserJpaEntity` porque
`spring.jpa.hibernate.ddl-auto=update` está configurado. Esto actualiza la
estructura, pero no crea cuentas ni transforma contraseñas existentes.

Cada cuenta debe tener un `username` único, una contraseña codificada con BCrypt
y un rol exactamente igual a `ADMIN`, `VENDEDOR` o `BODEGUERO`. No guardes la
contraseña en texto plano. Después de generar un hash BCrypt con
`BCryptPasswordEncoder` de Spring Security, puedes insertar una cuenta así:

```sql
INSERT INTO users (username, password, role)
VALUES ('admin', '<hash-BCrypt-generado>', 'ADMIN');
```

El valor `<hash-BCrypt-generado>` es un marcador: reemplázalo por el hash real
antes de ejecutar el SQL. No existe actualmente un endpoint público de registro
de usuarios.

### Variables de entorno

Configura estas variables en el servicio backend de Render:

| Variable | Uso |
|---|---|
| `SPRING_DATASOURCE_URL` | URL JDBC de PostgreSQL/Neon. |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos. |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos. |
| `JWT_SECRET` | Clave secreta de al menos 32 bytes UTF-8 para firmar JWT. Usa un valor aleatorio y privado; no lo subas al repositorio. |
| `JWT_EXPIRATION_MS` | Duración del token en milisegundos; opcional, predeterminado `3600000` (una hora). |
| `FRONTEND_ORIGIN` | Origen público del frontend, por ejemplo `https://mi-app.vercel.app`. |
| `PORT` | Puerto asignado por Render; se configura automáticamente en Render. |

Si despliegas el frontend por separado (por ejemplo, en Vercel), configura en el
proyecto frontend la variable `VITE_API_BASE_URL` con el origen del backend
Render, por ejemplo `https://demo1-zbrl.onrender.com` (sin `/api` al final).
Vite incorpora esta variable durante el build, así que vuelve a desplegar el
frontend después de cambiarla. En desarrollo local, el proxy de Vite enruta `/api`
al backend configurado en `frontend/vite.config.ts`; con Docker Compose, Nginx
también enruta `/api` al contenedor backend.

La lista de revocación del logout es solo en memoria: se borra al reiniciar el
backend y no se comparte entre varias instancias. Para despliegues con varias
instancias o revocación persistente, se necesitaría una lista compartida, por
ejemplo en Redis o en PostgreSQL.

## Despliegue en Render y Vercel

En el servicio backend de Render, configura la variable `FRONTEND_ORIGIN` con el
origen público del frontend de Vercel (por ejemplo, `https://mi-app.vercel.app`,
sin una ruta al final), además de las variables de conexión a Neon y `JWT_SECRET`
descritas arriba. El backend usa el origen para permitir solicitudes CORS a sus
rutas `/api/**`. Si cambias el dominio público del frontend, actualiza esta
variable y vuelve a desplegar el backend.
