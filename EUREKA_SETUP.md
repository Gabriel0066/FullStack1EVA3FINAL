# Guía de Uso - Eureka Service Discovery 🔍

Tu proyecto ha sido configurado completamente con **Netflix Eureka** para descubrimiento de servicios.

## 📋 Estructura del Proyecto

```
FullStack1EVA3-main/
├── eureka-server/                    ← Servidor de descubrimiento
├── delivery-mascotas/                ← Cliente Eureka
└── personal-medico/                  ← Cliente Eureka
```

## 🚀 Instrucciones de Ejecución

### 1️⃣ **Iniciar Eureka Server PRIMERO**

```bash
cd eureka-server
mvn clean install
mvn spring-boot:run
```

✅ Eureka estará disponible en: **http://localhost:8761/**

### 2️⃣ **Iniciar los Microservicios** (en terminales diferentes)

**Terminal 2 - Personal Médico:**
```bash
cd personal-medico
mvn spring-boot:run
```
✅ Disponible en: http://localhost:8081/

**Terminal 3 - Delivery Mascotas:**
```bash
cd delivery-mascotas
mvn spring-boot:run
```
✅ Disponible en: http://localhost:8082/

## 🔍 Verificar que los servicios estén registrados

1. Abre tu navegador en: **http://localhost:8761/**
2. Deberías ver ambos microservicios listados como "Application"

```
Application: DELIVERY-MASCOTAS-MICROSERVICE  ✅
Application: PERSONAL-MEDICO-MICROSERVICE    ✅
```

## 📊 Cambios Realizados

### ✅ Eureka Server Creado
- `eureka-server/` con configuración completa
- Puerto: **8761**
- No se registra a sí mismo (configurado como servidor puro)

### ✅ Ambos Microservicios Actualizados

**delivery-mascotas/**
- ✅ Dependencia Eureka Client agregada en pom.xml
- ✅ Anotación `@EnableDiscoveryClient` en clase Application
- ✅ Configuración Eureka en application.properties

**personal-medico/**
- ✅ Dependencia Eureka Client agregada en pom.xml
- ✅ Anotación `@EnableDiscoveryClient` en clase Application
- ✅ Configuración Eureka en application.properties

## 🔗 Propiedades Configuradas

Todas los servicios tienen estas propiedades en `application.properties`:

```properties
# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.hostname=localhost
eureka.instance.prefer-ip-address=false
eureka.instance.lease-renewal-interval-in-seconds=10
eureka.instance.lease-expiration-duration-in-seconds=30
```

## 📞 Próximos Pasos

### Comunicación entre Servicios (Feign Clients)

Ya tienes **spring-cloud-starter-openfeign** configurado. Para llamar desde un servicio a otro, usa:

```java
@FeignClient(name = "personal-medico-microservice")
public interface PersonalClient {
    @GetMapping("/api/personal/{id}")
    PersonalDTO getPersonal(@PathVariable Long id);
}
```

### Ejemplo: Desde delivery-mascotas llamar a personal-medico

```java
@Service
public class TrasladoService {
    
    @Autowired
    private PersonalClient personalClient;
    
    public void asignarPersonal(Long trasladoId, Long personalId) {
        PersonalDTO personal = personalClient.getPersonal(personalId);
        // Tu lógica aquí
    }
}
```

## ⚙️ Configuración Adicional Opcional

### Habilitar Actuator Endpoints (ya configurado)

Los endpoints de salud están expuestos:
- http://localhost:8761/actuator/health
- http://localhost:8081/actuator/health
- http://localhost:8082/actuator/health

### Sincronización de Replicas (para producción)

Si quieres múltiples instancias del mismo servicio:

```properties
eureka.instance.instance-id=${spring.application.name}:${random.value}
```

## 🐛 Troubleshooting

### "No se ven los servicios en Eureka"
1. ✅ ¿Eureka Server está ejecutándose en puerto 8761?
2. ✅ ¿Los microservicios tienen `@EnableDiscoveryClient`?
3. ✅ ¿La URL de Eureka es correcta: `http://localhost:8761/eureka/`?

### "No puedo llamar entre servicios"
1. Verifica que Feign esté habilitado con `@EnableFeignClients`
2. El nombre del cliente debe coincidir con `spring.application.name`

### "Conexión rechazada"
1. Asegúrate de ejecutar Eureka Server PRIMERO
2. Espera 3-5 segundos antes de iniciar los microservicios

---

**¡Tu arquitectura de microservicios con Eureka está lista! 🎉**
