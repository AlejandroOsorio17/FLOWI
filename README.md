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

## Despliegue en Render y Vercel

En el servicio backend de Render, configura la variable `FRONTEND_ORIGIN` con el
origen público del frontend de Vercel (por ejemplo, `https://mi-app.vercel.app`,
sin una ruta al final). El backend usa ese origen para permitir solicitudes CORS
a sus rutas `/api/**`. Si cambias el dominio público del frontend, actualiza esta
variable y vuelve a desplegar el backend.
