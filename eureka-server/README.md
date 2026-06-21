# Eureka Server

Servidor de descubrimiento de servicios Netflix Eureka para la arquitectura de microservicios de la veterinaria.

## Características

- Registro central de microservicios
- Descubrimiento automático de servicios
- Health checks
- Balanceo de carga

## Ejecución

```bash
mvn spring-boot:run
```

## Acceso

- **URL**: http://localhost:8761/
- **Eureka Dashboard**: http://localhost:8761/eureka/web

## Configuración

- Puerto: 8761
- No se registra a sí mismo
- No trae el registro de otros servidores

## Microservicios registrados

- **personal-medico-microservice** (puerto 8081)
- **delivery-mascotas-microservice** (puerto 8082)
