# InmoVision 3D

Plataforma inmobiliaria construida con **Spring Boot + Thymeleaf** que permite publicar,
explorar y gestionar inmuebles, con visualización de planos 2D/3D, solicitudes de
contacto entre clientes y publicadores, favoritos, y un panel de administración con
reportes exportables (PDF, Excel, Word).

## Tabla de contenido

- [Stack tecnológico](#stack-tecnológico)
- [Requisitos previos](#requisitos-previos)
- [Puesta en marcha](#puesta-en-marcha)
- [Roles de usuario](#roles-de-usuario)
- [Estructura del proyecto](#estructura-del-proyecto)
- [Rutas principales](#rutas-principales)
- [Módulo de reportes](#módulo-de-reportes)

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Backend | Java 21, Spring Boot 4.1.1 (Web MVC, Data JPA, Security, Validation) |
| Vistas | Thymeleaf + thymeleaf-extras-springsecurity6 |
| Base de datos | MariaDB 10.6+ (driver `mariadb-java-client`) |
| Reportes | Apache POI (Excel), OpenPDF (PDF), Apache POI (Word) |
| Utilidades | Lombok |
| Build | Maven |

## Requisitos previos

- **JDK 21** o superior
- **Maven** (o usar el wrapper `mvnw` / `mvnw.cmd` incluido en el proyecto)
- **MariaDB** (o MySQL compatible) corriendo en `localhost:3306`

## Puesta en marcha

### 1. Crear la base de datos

El proyecto usa `ddl-auto: update`, así que solo necesitas crear el esquema vacío;
Hibernate genera y actualiza las tablas automáticamente al arrancar.

```sql
CREATE DATABASE inmovision3d;
```

Con Docker, como alternativa rápida:

```bash
docker run --name inmovision-db \
  -e MARIADB_ROOT_PASSWORD= \
  -e MARIADB_DATABASE=inmovision3d \
  -p 3306:3306 -d mariadb:10.6
```

### 2. Configurar credenciales (si aplica)

Por defecto, `src/main/resources/application.yaml` apunta a:

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/inmovision3d
    username: root
    password: ""
```

Si tu instalación de MariaDB usa otro usuario o contraseña, actualiza esos valores.

### 3. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

La aplicación queda disponible en **http://localhost:8080**.

### 4. Crear el primer usuario ADMIN

Por seguridad, el registro público (`/auth/registro`) solo permite crear cuentas con
rol `CLIENTE` o `PUBLICADOR`. El primer usuario `ADMIN` se asigna manualmente en la
base de datos:

```sql
UPDATE usuarios SET rol = 'ADMIN' WHERE email = 'tu_correo@ejemplo.com';
```

Desde ahí, ese usuario ya puede promover a otros desde el panel de administración
(`/admin/dashboard`, pestaña Usuarios).

## Roles de usuario

| Rol | Puede |
|---|---|
| `CLIENTE` | Explorar inmuebles, marcar favoritos, enviar solicitudes de contacto |
| `PUBLICADOR` | Todo lo anterior + publicar/editar sus propios inmuebles, subir planos 2D/3D, gestionar solicitudes recibidas |
| `ADMIN` | Todo lo anterior + panel de administración: gestión de usuarios y roles, gestión de todos los inmuebles y solicitudes, generación de reportes |

## Estructura del proyecto

```
src/main/java/com/InmoVision3D/
├── controller/     Controladores MVC (vistas) y REST (@RestController bajo /api)
├── service/        Lógica de negocio (interfaces) + service/impl (implementaciones)
├── service/Reportes/  Generación de reportes (PDF, Excel, Word) y filtros (Specification)
├── repository/     Repositorios Spring Data JPA
├── model/           Entidades JPA (Usuario, Inmueble, Solicitud, Favorito, Plano2D, ImagenInmueble)
├── model/enums/     RolUsuario, EstadoInmueble, EstadoSolicitud, TipoInmueble, TipoOperacion
├── dto/             Objetos de transferencia (filtros de reportes, estadísticas)
├── security/        Configuración de Spring Security, UserDetailsService
├── exception/       Excepciones de negocio y manejador global
└── config/          Configuración adicional (recursos estáticos, uploads)

src/main/resources/
├── templates/       Vistas Thymeleaf (home, auth, inmuebles, usuario, Admin, planos)
├── static/          CSS, JS e imágenes
└── application.yaml Configuración de datasource, JPA y subida de archivos
```

## Rutas principales

| Ruta | Descripción | Acceso |
|---|---|---|
| `/` | Página de inicio | Público |
| `/inmuebles` | Catálogo de inmuebles | Público |
| `/inmuebles/{id}` | Detalle de un inmueble | Público |
| `/auth/login`, `/auth/registro` | Login y registro | Público |
| `/inmuebles/publicar` | Publicar inmueble | PUBLICADOR, ADMIN |
| `/usuario/perfil` | Perfil del usuario | Autenticado |
| `/usuario/favoritos` | Favoritos guardados | CLIENTE, ADMIN |
| `/usuario/mis-inmuebles` | Inmuebles propios | PUBLICADOR, ADMIN |
| `/solicitudes` | Solicitudes enviadas/recibidas | CLIENTE, PUBLICADOR, ADMIN |
| `/planos/editor/{inmuebleId}` | Editor de plano 2D | PUBLICADOR, ADMIN |
| `/planos/visor3d/{inmuebleId}` | Visor 3D del plano | Según el inmueble |
| `/admin/dashboard` | Panel de administración | ADMIN |
| `/admin/reportes` | Centro de reportes | ADMIN |
| `/api/**` | API REST (JSON) usada por las vistas | Ver `SecurityConfig` |

## Módulo de reportes

Desde `/admin/reportes`, el panel de administración permite generar 4 tipos de
reporte, cada uno exportable en **PDF, Excel o Word**:

- **Inventario**: listado plano de inmuebles según los filtros aplicados.
- **Por tipo**: agrupado por tipo de inmueble (casa, apartamento, local, etc.).
- **Precios**: análisis de precios por tipo de inmueble.
- **Por publicador**: resumen de inmuebles agrupados por publicador.

Los filtros son multicriterio y combinables: estado, tipo de operación (venta/arriendo),
tipo de inmueble, publicador y texto de búsqueda libre (título o ubicación).
