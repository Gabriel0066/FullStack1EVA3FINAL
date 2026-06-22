# Sistema de Gestión Veterinaria - Microservicios Backend

Arquitectura de microservicios para la gestión de una veterinaria. Proyecto backend desarrollado con **Java 17**, **Spring Boot 3.3.0** y **Spring Cloud 2023.0.1**.

## Arquitectura

```
                            ┌─────────────────┐
                            │   API Gateway    │  :8080
                            │  (Spring Cloud   │
                            │   Gateway)       │
                            └────────┬─────────┘
                                     │
                    ┌────────────────┼────────────────┐
                    ▼                ▼                 ▼
            ┌──────────────┐ ┌─────────────┐ ┌──────────────┐
            │ Eureka Server│ │ Personal    │ │ Delivery     │
            │ (Discovery)  │ │ Médico      │ │ Mascotas     │
            │ :8761        │ │ :8081       │ │ :8082        │
            └──────────────┘ └──────┬──────┘ └──────┬───────┘
                                    │               │
                                    ▼               ▼
                            ┌──────────────┐ ┌──────────────┐
                            │  MySQL       │ │  MySQL       │
                            │ veterinaria_ │ │ veterinaria_ │
                            │ personal     │ │ delivery     │
                            └──────────────┘ └──────────────┘
```

| Servicio | Puerto | Descripción |
|---|---|---|
| **API Gateway** | `8080` | Puerta de entrada única a todos los microservicios |
| **Eureka Server** | `8761` | Servicio de descubrimiento y registro |
| **Personal Médico** | `8081` | CRUD del personal de la veterinaria |
| **Delivery Mascotas** | `8082` | Gestión de traslados de mascotas |
| **MySQL** | `3307` (host) / `3306` (container) | Base de datos relacional |

## Requisitos Previos

- **Docker** y **Docker Compose** (recomendado)
- O alternativamente: **Java 17+**, **Maven 3.9+**, **MySQL 8.0**

## Levantar el Proyecto con Docker (recomendado)

```bash
# Desde la raíz del proyecto
docker compose up --build
```

Esto construye e inicia todos los servicios en orden:
1. MySQL (con `init.sql` que crea las bases de datos)
2. Eureka Server
3. Personal Médico
4. Delivery Mascotas
5. API Gateway

Para detener:
```bash
docker compose down
```

Para eliminar también los volúmenes (borra datos persistentes):
```bash
docker compose down -v
```

## Levantar el Proyecto Manualmente (sin Docker)

### 1. Crear las bases de datos en MySQL

```sql
CREATE DATABASE IF NOT EXISTS veterinaria_personal;
CREATE DATABASE IF NOT EXISTS veterinaria_delivery;
```

### 2. Iniciar servicios en orden (4 terminales)

**Terminal 1 - Eureka Server:**
```bash
cd eureka-server
mvn clean install -DskipTests
mvn spring-boot:run
```

**Terminal 2 - API Gateway:**
```bash
cd api-gateway
mvn spring-boot:run
```

**Terminal 3 - Personal Médico:**
```bash
cd personal-medico
mvn spring-boot:run
```

**Terminal 4 - Delivery Mascotas:**
```bash
cd delivery-mascotas
mvn spring-boot:run
```

### 3. Script automatizado (Windows)

```powershell
.\start-all.ps1
```

### 4. Configuración desde VS Code

Abrir el proyecto en VS Code y usar la configuración compuesta **"Iniciar Todos"** en el panel de Run & Debug.

## URLs de Acceso

| Servicio | URL |
|---|---|
| **API Gateway** (Swagger UI) | http://localhost:8080/ |
| **Eureka Dashboard** | http://localhost:8761/ |
| **Personal Médico API** | http://localhost:8081/api/v1/personal |
| **Personal Médico Swagger** | http://localhost:8081/swagger-ui/index.html |
| **Delivery Mascotas API** | http://localhost:8082/api/v1/traslados |
| **Delivery Mascotas Swagger** | http://localhost:8082/swagger-ui/index.html |
| **Health Check Eureka** | http://localhost:8761/actuator/health |
| **Health Check Personal** | http://localhost:8081/actuator/health |
| **Health Check Delivery** | http://localhost:8082/actuator/health |
| **Health Check Gateway** | http://localhost:8080/actuator/health |

## Autenticación

Todos los endpoints (excepto Swagger, health y exists) requieren **HTTP Basic Auth**:

| Campo | Valor |
|---|---|
| Username | `admin` |
| Password | `admin123` |

Ejemplo con curl:
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/personal
```

## API Reference - Personal Médico (`/api/v1/personal`)

### Campos del recurso `Personal`

```json
{
  "idTrabajador": 1,
  "rol": "Veterinario",
  "nombre": "Juan",
  "apellido": "Pérez",
  "rut": "12345678-9",
  "correo": "juan@veterinaria.cl",
  "telefono": "+56912345678",
  "direccion": "Calle Principal 123, Santiago"
}
```

### Endpoints

#### GET /api/v1/personal — Listar todo el personal
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/personal
```
- Por gateway: `http://localhost:8080/api/v1/personal`
- Directo: `http://localhost:8081/api/v1/personal`
- Respuesta: `200 OK` con `CollectionModel<EntityModel<PersonalDTO>>` (HATEOAS)

#### GET /api/v1/personal/{id} — Obtener personal por ID
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/personal/1
```
- Respuesta: `200 OK` | `404 Not Found`

#### POST /api/v1/personal — Crear personal
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/personal \
  -H "Content-Type: application/json" \
  -d '{
    "rol": "Veterinario",
    "nombre": "Juan",
    "apellido": "Pérez",
    "rut": "12345678-9",
    "correo": "juan.perez@veterinaria.cl",
    "telefono": "+56912345678",
    "direccion": "Calle Principal 123, Santiago"
  }'
```
- Respuesta: `201 Created` con Location header | `400 Bad Request`

#### PUT /api/v1/personal/{id} — Actualizar personal
```bash
curl -u admin:admin123 -X PUT http://localhost:8080/api/v1/personal/1 \
  -H "Content-Type: application/json" \
  -d '{
    "rol": "Veterinario Senior",
    "nombre": "Juan",
    "apellido": "Pérez",
    "rut": "12345678-9",
    "correo": "juan.perez@veterinaria.cl",
    "telefono": "+56987654321",
    "direccion": "Avenida Principal 456, Santiago"
  }'
```
- Respuesta: `200 OK` | `404 Not Found`

#### DELETE /api/v1/personal/{id} — Eliminar personal
```bash
curl -u admin:admin123 -X DELETE http://localhost:8080/api/v1/personal/1
```
- Respuesta: `204 No Content` | `404 Not Found`

#### POST /api/v1/personal/migrate — Ejecutar migraciones Flyway
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/personal/migrate
```
- Respuesta: `200 OK` — `"Migraciones ejecutadas: 1"`

#### POST /api/v1/personal/seed/{count} — Generar datos falsos
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/personal/seed/10
```
- Respuesta: `200 OK` con `List<PersonalDTO>` de 10 registros generados con DataFaker

#### GET /api/v1/personal/exists/id/{id} — Verificar si existe por ID
```bash
curl http://localhost:8080/api/v1/personal/exists/id/1
```
- **Público** (no requiere auth)
- Respuesta: `{"exists": true, "_links": [...]}`

#### GET /api/v1/personal/exists/rut/{rut} — Verificar si existe RUT
```bash
curl http://localhost:8080/api/v1/personal/exists/rut/12345678-9
```
- **Público** (no requiere auth)
- Respuesta: `{"exists": true, "_links": [...]}`

#### GET /api/v1/personal/exists/correo/{correo} — Verificar si existe correo
```bash
curl http://localhost:8080/api/v1/personal/exists/correo/juan@veterinaria.cl
```
- **Público** (no requiere auth)
- Respuesta: `{"exists": true, "_links": [...]}`

## API Reference - Delivery Mascotas (`/api/v1/traslados`)

### Campos del recurso `Traslado`

```json
{
  "idTraslado": 1,
  "idPaciente": 100,
  "idTrabajador": 1,
  "direccionHogar": "Av. Siempre Viva 742, Santiago",
  "horaRecogida": "10:30",
  "estado": "PENDIENTE"
}
```

### Endpoints

#### GET /api/v1/traslados — Listar todos los traslados
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/traslados
```
- Por gateway: `http://localhost:8080/api/v1/traslados`
- Directo: `http://localhost:8082/api/v1/traslados`
- Respuesta: `200 OK` con `CollectionModel<EntityModel<Traslado>>` (HATEOAS)

#### GET /api/v1/traslados/{id} — Obtener traslado por ID
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/traslados/1
```
- Respuesta: `200 OK` | `404 Not Found`

#### POST /api/v1/traslados — Crear traslado
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/traslados \
  -H "Content-Type: application/json" \
  -d '{
    "idPaciente": 100,
    "idTrabajador": 1,
    "direccionHogar": "Av. Siempre Viva 742, Santiago",
    "horaRecogida": "10:30",
    "estado": "PENDIENTE"
  }'
```
- Valida que `idTrabajador` exista en Personal Médico vía FeignClient
- Respuesta: `201 Created` | `400 Bad Request` (trabajador no existe o datos inválidos)

#### PUT /api/v1/traslados/{id}/estado/{nuevoEstado} — Actualizar estado
```bash
curl -u admin:admin123 -X PUT http://localhost:8080/api/v1/traslados/1/estado/COMPLETADO
```
- Respuesta: `200 OK` | `404 Not Found`

#### DELETE /api/v1/traslados/{id} — Eliminar traslado
```bash
curl -u admin:admin123 -X DELETE http://localhost:8080/api/v1/traslados/1
```
- Respuesta: `204 No Content` | `404 Not Found`

#### POST /api/v1/traslados/migrate — Ejecutar migraciones Flyway
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/traslados/migrate
```
- Respuesta: `200 OK`

#### POST /api/v1/traslados/seed/{count} — Generar datos falsos
```bash
curl -u admin:admin123 -X POST http://localhost:8080/api/v1/traslados/seed/10
```
- Respuesta: `200 OK` con `List<Traslado>` de 10 registros

#### GET /api/v1/traslados/estadisticas/estado/{estado} — Contar por estado
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/traslados/estadisticas/estado/PENDIENTE
```
- Respuesta: `{"count": 5, "_links": [...]}`

#### GET /api/v1/traslados/estadisticas/trabajador/{id}/estado/{estado} — Contar por trabajador y estado
```bash
curl -u admin:admin123 http://localhost:8080/api/v1/traslados/estadisticas/trabajador/1/estado/PENDIENTE
```
- Respuesta: `{"count": 3, "_links": [...]}`

## Datos Semilla

Flyway inserta datos iniciales automáticamente al iniciar:

### Personal Médico (4 registros):
| ID | Rol | Nombre | RUT |
|---|---|---|---|
| 1 | Veterinario | Carlos | 11111111-1 |
| 2 | Asistente | María | 22222222-2 |
| 3 | Recepcionista | Pedro | 33333333-3 |
| 4 | Conductor | Ana | 44444444-4 |

### Traslados (5 registros):
| ID | Paciente | Trabajador | Estado |
|---|---|---|---|
| 1 | 101 | 1 | PENDIENTE |
| 2 | 102 | 2 | COMPLETADO |
| 3 | 103 | 3 | EN_CAMINO |
| 4 | 104 | 4 | PENDIENTE |
| 5 | 105 | 1 | CANCELADO |

## Variables de Entorno

Archivo `.env` en la raíz:

```env
MYSQL_ROOT_PASSWORD=root
MYSQL_PORT=3306
```

## Base de Datos

MySQL corre en el puerto `3307` del host (mapeado al `3306` del contenedor).

| Base de datos | Microservicio |
|---|---|
| `veterinaria_personal` | Personal Médico |
| `veterinaria_delivery` | Delivery Mascotas |

Conexión directa desde el host:
```
Host: localhost
Puerto: 3307
Usuario: root
Password: root
```

## Tests

```bash
# Tests de cada módulo
cd personal-medico && mvn test
cd delivery-mascotas && mvn test
```

Los tests usan H2 en memoria (no requieren MySQL).

## Stack Tecnológico

- **Java 17** + **Spring Boot 3.3.0**
- **Spring Cloud 2023.0.1** (Eureka, Gateway, OpenFeign)
- **Spring Data JPA** + **Flyway** (migraciones)
- **MySQL 8.0**
- **Spring Security** (HTTP Basic con usuarios en memoria)
- **Spring HATEOAS** (respuestas REST con hipervínculos)
- **Springdoc OpenAPI** (Swagger UI)
- **Lombok**
- **DataFaker** (generación de datos de prueba)
- **JUnit 5** + **H2** (tests de integración)
- **Docker** / **Docker Compose**
