# 🌐 SistemaRutasprime Backend

El proyecto **RutasPrime Backend** es la parte central del sistema **RutasPrime**, encargada de manejar toda la **lógica del servidor**, la **seguridad de los datos** y la **comunicación con la base de datos**.

Desarrollado con **Spring Boot 3.5.6** y **Java 17**, este backend proporciona una **arquitectura robusta, modular y escalable**, ideal para aplicaciones empresariales orientadas a servicios RESTful.

---

## ⚙️ Principales funcionalidades

- **Gestión de usuarios:** registro, edición, eliminación y recuperación de cuentas.
- **Autenticación y autorización seguras** mediante **Spring Security** y **JWT (JSON Web Tokens)**.
- **Verificación por código OTP (One-Time Password)** a través de **correo electrónico**.
- **Módulo de administración de conductores y clientes**, con validaciones de campos y control de estados.
- **Exportación de datos a Excel** mediante **Apache POI**.
- **Envío automatizado de notificaciones por correo** usando **Spring Mail** y **plantillas HTML** con **Thymeleaf**.
- **Manejo avanzado de logs y auditorías** con **Logback**.
- **Soporte multizona horaria** y formato de fechas personalizados.

---

## 🧩 Tecnologías principales

- **Spring Boot**
- **Spring Data JPA**
- **Spring Security**
- **Spring Mail**
- **Hibernate**
- **MySQL**
- **JWT** (autenticación basada en tokens)
- **Lombok**
- **Apache Commons**
- **Google Guava**
- **Thymeleaf**
- **Apache POI**

---

## 🧪 Pruebas y mantenimiento

Incluye dependencias para pruebas unitarias e integradas con:
- **JUnit 5**
- **Mockito**
- **AssertJ**
- **Spring Security Test**

Estas herramientas garantizan la calidad, estabilidad y fiabilidad del sistema.

---

## 🧾 Registro y monitoreo

El sistema utiliza **Logback** para la gestión de logs, permitiendo el seguimiento de eventos, errores, advertencias e información del sistema.  
Se puede configurar la rotación de logs y el almacenamiento diario para un mantenimiento más eficiente.

---

📍 **Autor:** Equipo de Desarrollo RutasPrime  
📅 **Versión:** 1.0.0  
💡 **Lenguaje:** Java 17  
🚀 **Framework principal:** Spring Boot 3.5.6


---

## 📖 Tabla de Contenidos

- [✨ Características](#-Características-del-Backend-----Sistema-RutasPrime)
- [⚙️ Dependencias](#-Dependencias-del-Proyecto-RutasPrime-Backend)
- [📂 Estructura del Proyecto](#-estructura-del-proyecto)
- [🛠️ Configuración](#-configuración)
- [▶️ Ejecución](#-ejecución)
- [📬 Test con Postman](#-test-con-postman)
- [🌱 Comandos Git básicos](#-comandos-git-básicos)
- [🗄️ Base de Datos](#-base-de-datos)
- [🛡️ Seguridad](#-seguridad)
- [💡 Notas finales](#-notas-finales)

---

# 🚀 **Características del Backend --- Sistema RutasPrime**

Este backend implementa un sistema robusto y seguro de **gestión de
usuarios, autenticación, validación documental y mensajería**,
construido con **Spring Boot, Spring Security, JWT, y MySQL**.

------------------------------------------------------------------------

## 🔑 **Gestión y Seguridad de Usuarios**

-   🔒 **Registro y login** de usuarios con cifrado de contraseñas
    mediante `BCryptPasswordEncoder`.\
-   📧 **Validación de identidad con OTP (One Time Password)** enviado
    al correo electrónico.\
-   📨 **Envío de correos HTML personalizados** (plantillas:
    `otp-register.html`, `otp-reset.html`, `welcome.html`,
    `password-changed.html`, `profile-updated.html`).\
-   👤 **Perfil de usuario protegido con JWT**, gestionado por filtros
    de seguridad.\
-   🛡️ **Autenticación y autorización** implementadas con **Spring
    Security + JWT**.\
-   🗄️ **Persistencia con JPA/Hibernate** sobre base de datos
    **MySQL**.\
-   🔁 **Reenvío automático de códigos OTP** para registro o
    recuperación de contraseña.\
-   ⚙️ **Validación en tiempo real con RENIEC** mediante
    `DniValidatorService`.\
-   🧩 **Roles y jerarquías de usuarios:**
    -   `ROLE_CLIENTE`
    -   `ROLE_CONDUCTOR`
    -   `ROLE_ADMIN`
    -   `ROLE_SUPERADMIN`

------------------------------------------------------------------------

## 👤 **Control de Usuarios (UserController)**

-   📄 **Perfil de usuario:** obtener datos del usuario autenticado.\
-   ✏️ **Actualización de perfil** con notificación automática al
    correo.\
-   🔐 **Cambio seguro de contraseña** con validación de la actual y
    correo de confirmación.\
-   📋 **Listados segmentados:**
    -   Clientes (`/clientes`)\
    -   Conductores y Clientes (`/conductores-clientes`)\
    -   Administradores y Superadministradores (`/admins`)
-   📤 **Exportación a Excel (.xlsx)** de todos los usuarios del
    sistema.\
-   🔎 **Consulta individual de usuario por ID**.

------------------------------------------------------------------------

## 🚗 **Gestión de Conductores (ConductorController)**

-   🧾 **Solicitud de registro como conductor**, con subida de múltiples
    documentos:
    -   Foto del conductor y licencia\
    -   Licencia de conducir\
    -   Antecedentes penales\
    -   Tarjeta de propiedad\
    -   Tarjeta de circulación\
    -   SOAT\
    -   Revisión técnica\
-   ⏳ **Seguimiento del estado de verificación** de la solicitud.\
-   🧠 **Verificación y observaciones administrativas** por parte de
    roles `ADMIN` o `SUPERADMIN`.\
-   🕒 **Historial de cambios de estado** de solicitudes.\
-   📋 **Listado completo de todas las solicitudes** de conductores.

------------------------------------------------------------------------

## 📬 **Mensajería de Contacto (ContactController)**

-   💌 **Formulario "Contáctanos"** para envío de mensajes desde el
    frontend.\
-   🔁 **Respuestas automáticas por correo electrónico** a los
    usuarios.\
-   🔍 **Consulta individual de mensaje** por código único.\
-   🗂️ **Listado completo de mensajes recibidos** (solo `ADMIN` o
    `SUPERADMIN`).

------------------------------------------------------------------------

## 🗂️ **Gestión de Archivos (ArchivoController)**

-   📁 **Descarga y visualización de archivos** de conductores
    almacenados en la carpeta `uploads/`.\
-   🧱 **Manejo de rutas dinámicas** por DNI y nombre del archivo.\
-   🧩 **Carga de imágenes por defecto (`sin-archivo.png`)** si el
    recurso no existe.\
-   🔒 **Protección de rutas y acceso mediante CORS habilitado** para el
    frontend.

------------------------------------------------------------------------

## ⚙️ **Administración (AuthAdminController)**

-   🔐 **Login exclusivo para administradores y superadministradores.**\
-   🧑‍💼 **Registro de nuevos administradores** (solo `SUPERADMIN`).\
-   🧾 **Generación automática de JWT tokens** con roles
    administrativos.

------------------------------------------------------------------------

## 🌐 **Autenticación Pública (AuthPublicController)**

-   🆕 **Registro de nuevos usuarios (clientes)** con validación RENIEC
    y OTP.\
-   📩 **Activación de cuenta vía correo electrónico.**\
-   🔁 **Reenvío de OTPs caducados o perdidos.**\
-   🔑 **Login seguro con JWT (para CLIENTE o CONDUCTOR).**\
-   🔄 **Recuperación y restablecimiento de contraseñas** mediante OTP.\
-   ⚠️ **Bloqueo de usuarios inactivos o no verificados.**

------------------------------------------------------------------------

## 🧾 **Otras Características Técnicas**

-   🧰 **Configuración centralizada** en `application.properties` (base
    de datos, correo, JWT, uploads, etc.).\
-   ⏱️ **Formato de fecha y zona horaria** definidos (America/Bogotá).\
-   🧮 **Logs y monitoreo** con **Logback** (rotación diaria, registro
    de errores, advertencias e información del sistema).\
-   🧩 **Mensajería estructurada con `ApiResponse` y DTOs
    personalizados.**\
-   💾 **Inicialización automática de base de datos** con
    `spring.sql.init.mode=always`.

---

# ⚙️ Dependencias del Proyecto RutasPrime Backend

Estas son las principales librerías y frameworks utilizados en el **backend del sistema RutasPrime**, junto con su función dentro del proyecto.

---

## 🧩 Core de Spring Boot

- **Spring Boot Starter Web** → Permite crear y exponer APIs REST con Spring MVC.
- **Spring Boot Starter Data JPA** → Facilita la persistencia de datos con Hibernate/JPA.
- **MySQL Connector J** → Controlador JDBC para conectar el backend con la base de datos MySQL.

---

## 🔐 Seguridad y Autenticación

- **Spring Boot Starter Security** → Proporciona autenticación, autorización y cifrado.
- **JJWT (io.jsonwebtoken)** → Gestión de tokens JWT (creación, validación y decodificación).  
  Incluye:
    - `jjwt-api`
    - `jjwt-impl`
    - `jjwt-jackson` (usa Jackson para serializar/deserializar los claims).

---

## 📧 Correo y Validaciones

- **Spring Boot Starter Mail** → Permite el envío de correos electrónicos (verificación OTP, recuperación de contraseña, notificaciones).
- **Spring Boot Starter Validation** → Implementa validaciones con Jakarta Bean Validation.
- **Thymeleaf** → Motor de plantillas HTML usado para generar correos personalizados.

---

## 🧠 Utilidades y Ayudas

- **Lombok** → Reduce el código repetitivo (getters, setters, constructores, logs, etc.).
- **Google Guava** → Proporciona utilidades adicionales de alto rendimiento (colecciones, strings, cachés).
- **Apache POI (poi-ooxml)** → Generación y lectura de archivos Excel (XLSX).
- **Apache Commons Lang3** → Funciones avanzadas para manejo de cadenas y objetos.
- **Commons IO** → Utilidades para lectura y escritura de archivos.

---

## 🧪 Pruebas (Testing)

- **Spring Boot Starter Test** → Librería principal para pruebas unitarias e integración (JUnit 5 incluido).
- **Spring Security Test** → Pruebas específicas de autenticación y roles.
- **Mockito Core** y **Mockito JUnit Jupiter** → Simulación (mocking) de dependencias en tests.
- **AssertJ** → Framework con sintaxis fluida para aserciones legibles.

---

## 🧾 Registro y Monitoreo

- **Logback** *(incluido por defecto en Spring Boot)* → Sistema de logging centralizado que permite registrar errores, advertencias e información del sistema.
    - Permite definir rutas de logs personalizados.
    - Soporta rotación y almacenamiento diario.
    - Se configura mediante el archivo `logback-spring.xml`.

---

## ⚙️ Plugins de Compilación

- **Spring Boot Maven Plugin** → Permite ejecutar y empaquetar la aplicación.
- **Maven Compiler Plugin (Java 17)** → Configura la compilación con la versión Java 17.

---


## 📂 Estructura del Proyecto

```bash
INTEGRADOR_BACK/
├── .idea/                                          # Configuración de IntelliJ IDEA (solo entorno local)
├── .mvn/                                           # Wrapper de Maven (para compilar sin instalación global)
├── ScriptsBD/                                      # Scripts SQL y respaldos de la base de datos
├── uploads/                                        # Carpeta donde se guardan archivos subidos (fotos, docs)
│
├── src/
│   ├── main/
│   │   ├── java/com/backend/avance1/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java             # Configura seguridad, JWT, CORS, roles y accesos
│   │   │   │
│   │   │   ├── controller/                         # Controladores REST: manejan peticiones HTTP
│   │   │   │   ├── ArchivoController.java          # Subida y descarga de archivos
│   │   │   │   ├── AuthAdminController.java        # Login y gestión exclusiva para administradores
│   │   │   │   ├── AuthPublicController.java       # Registro y recuperación pública con OTP/email
│   │   │   │   ├── ConductorController.java        # CRUD de conductores
│   │   │   │   ├── ContactController.java          # Envío/respuesta de mensajes de contacto
│   │   │   │   └── UserController.java             # Operaciones sobre usuarios (perfil, contraseña, etc.)
│   │   │   │
│   │   │   ├── dto/                                # DTOs: estructuras para transferencia de datos
│   │   │   │   ├── ApiResponse.java                # Respuesta estándar de la API (mensaje, estado, data)
│   │   │   │   ├── ChangePasswordDTO.java          # Datos para cambiar contraseña
│   │   │   │   ├── ConductorInfoDTO.java           # Datos para registrar/editar conductores
│   │   │   │   ├── ConductorInfoResponseDTO.java   # Respuesta detallada del conductor
│   │   │   │   ├── ContactDetailDTO.java           # Detalle de un mensaje de contacto
│   │   │   │   ├── ContactDTO.java                 # Datos del formulario de contacto
│   │   │   │   ├── ContactReplyDTO.java            # Datos de respuesta del admin
│   │   │   │   ├── ResetPasswordDTO.java           # Datos para restablecer contraseña
│   │   │   │   ├── UpdateUserDTO.java              # Datos para actualizar perfil de usuario
│   │   │   │   └── UserDTO.java                    # Datos básicos del usuario
│   │   │   │
│   │   │   ├── entity/                             # Entidades JPA (tablas en la BD)
│   │   │   │   ├── ConductorInfo.java              # Información principal del conductor
│   │   │   │   ├── ConductorInfoHistorial.java     # Historial de cambios de conductores
│   │   │   │   ├── ContactMessage.java             # Mensaje enviado desde formulario de contacto
│   │   │   │   ├── ContactReply.java               # Respuestas de contacto enviadas por el admin
│   │   │   │   ├── EstadoVerificacion.java         # Enum de estados (APROBADO, RECHAZADO, PENDIENTE)
│   │   │   │   ├── Otp.java                        # Entidad que guarda los códigos OTP
│   │   │   │   ├── RoleName.java                   # Enum con roles del sistema (ADMIN, CLIENTE, etc.)
│   │   │   │   └── User.java                       # Entidad principal de usuario
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   └── GlobalExceptionHandler.java     # Manejo global de excepciones HTTP
│   │   │   │
│   │   │   ├── repository/                         # Repositorios JPA (interacción con la BD)
│   │   │   │   ├── ConductorInfoHistorialRepository.java  # CRUD del historial de conductores
│   │   │   │   ├── ConductorInfoRepository.java           # CRUD de conductores
│   │   │   │   ├── ContactReplyRepository.java            # CRUD de respuestas de contacto
│   │   │   │   ├── ContactRepository.java                 # CRUD de mensajes de contacto
│   │   │   │   ├── OtpRepository.java                     # CRUD de códigos OTP
│   │   │   │   └── UserRepository.java                    # CRUD de usuarios
│   │   │   │
│   │   │   ├── scheduler/
│   │   │   │   └── UserCleanupTask.java            # Tarea programada para limpiar usuarios inactivos
│   │   │   │
│   │   │   ├── security/                           # Seguridad JWT
│   │   │   │   ├── JwtAuthEntryPoint.java          # Maneja accesos no autorizados (401)
│   │   │   │   ├── JwtAuthFilter.java              # Filtro para validar tokens JWT
│   │   │   │   └── JwtUtil.java                    # Utilidades para crear y verificar tokens JWT
│   │   │   │
│   │   │   ├── service/                            # Lógica de negocio principal
│   │   │   │   ├── ConductorInfoService.java       # Gestión de conductores
│   │   │   │   ├── ConductorInfoServiceInterface.java  # Interfaz de servicio de conductores
│   │   │   │   ├── ContactService.java             # Gestión de mensajes de contacto
│   │   │   │   ├── ContactServiceInterface.java    # Interfaz de ContactService
│   │   │   │   ├── DniService.java                 # Consulta API externa de DNI
│   │   │   │   ├── DniValidatorService.java        # Validación de formato y existencia de DNI
│   │   │   │   ├── DniValidatorServiceInterface.java # Interfaz del validador de DNI
│   │   │   │   ├── FileStorageService.java         # Maneja guardado/eliminación de archivos locales
│   │   │   │   ├── FileStorageServiceInterface.java # Interfaz del servicio de archivos
│   │   │   │   ├── MailService.java                # Envío de correos electrónicos HTML
│   │   │   │   ├── MailServiceInterface.java       # Interfaz de MailService
│   │   │   │   ├── OtpService.java                 # Generación y validación de códigos OTP
│   │   │   │   ├── OtpServiceInterface.java        # Interfaz del servicio OTP
│   │   │   │   ├── UserExcelService.java           # Exporta usuarios a archivos Excel
│   │   │   │   ├── UserExcelServiceInterface.java  # Interfaz del servicio Excel
│   │   │   │   ├── UserService.java                # Lógica central de usuarios
│   │   │   │   └── UserServiceInterface.java       # Interfaz del servicio de usuarios
│   │   │   │
│   │   │   ├── utils/                              # Utilidades compartidas
│   │   │   │   ├── TextFormatUtil.java             # Formatea textos (capitalización, limpieza, DNI, etc.)
│   │   │   │   ├── TextUtils.java                  # Operaciones de texto genéricas (validez, tamaño, etc.)
│   │   │   │   └── PasswordGenerator.java          # Genera contraseñas seguras con mayúsculas, minúsculas y símbolos
│   │   │   │
│   │   │   └── Avance1Application.java             # Clase principal que arranca Spring Boot (método main)
│   │   │
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── logo.jpg                        # Logo usado en correos o reportes PDF
│   │       │   └── sin-archivo.png                 # Imagen por defecto cuando falta un archivo
│   │       │
│   │       ├── templates/                          # Plantillas HTML de correos
│   │       │   ├── conductor-apply.html            # Correo de solicitud de registro de conductor
│   │       │   ├── conductor-aprobado.html         # Correo de aprobación
│   │       │   ├── conductor-confirmacion.html     # Confirmación de recepción
│   │       │   ├── conductor-rechazado.html        # Notificación de rechazo
│   │       │   ├── contact-email.html              # Mensaje de contacto recibido
│   │       │   ├── contact-reply.html              # Respuesta de contacto enviada
│   │       │   ├── otp-register.html               # OTP para registro de usuario
│   │       │   ├── otp-reset.html                  # OTP para recuperación de contraseña
│   │       │   ├── password-changed.html           # Notificación de cambio de contraseña
│   │       │   ├── profile-updated.html            # Correo de actualización de perfil
│   │       │   └── welcome.html                    # Correo de bienvenida
│   │       │
│   │       └── application.properties              # Configuración de DB, JWT, email y servidor
│   │
│   └── test/                                       # Pruebas unitarias e integración
│       └── java/com/backend/avance1/
│           ├── controller/
│           │   ├── AuthAdminControllerTest.java
│           │   ├── AuthPublicControllerTest.java
│           │   ├── ConductorControllerTest.java
│           │   ├── ContactControllerTest.java
│           │   └── UserControllerTest.java
│           │
│           ├── service/
│           │   ├── BaseServiceTest.java
│           │   ├── ConductorInfoServiceTest.java
│           │   ├── DniValidatorServiceTest.java
│           │   ├── FileStorageServiceTest.java
│           │   ├── MailServiceTest.java
│           │   ├── OtpServiceTest.java
│           │   └── UserServiceTest.java
│           │
│           └── Avance1ApplicationTests.java        # Prueba del contexto general de la app
│
├── data.sql                                       # Script con datos iniciales (roles, usuarios, etc.)
└── pom.xml                                        # Configuración principal de dependencias y build de Maven
```

---

## 🛠️ Configuración

### 1. Base de datos
Crea una base de datos vacía, por ejemplo:

```sql
CREATE DATABASE rutasprimebackend;
```

👉 No es necesario crear tablas: **Hibernate las generará automáticamente**.

### 2. Archivo `application.properties`
Configura tus credenciales de MySQL y correo en:

```properties
spring.application.name=avance1
spring.datasource.url=jdbc:mysql://localhost:3306/rutasprimebackend?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.sql.init.mode=always
spring.sql.init.encoding=UTF-8
spring.jpa.defer-datasource-initialization=true
# JWT
app.jwt.secret=K8hS2m9Qp4Xr7T0aVz3Nc5Lg8Dy6Jf1W
app.jwt.expiration=3600000

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Correo del administrador contáctanos
app.mail.admin=

# Correo para recibir solicitudes conductor
app.conductor.solicitudes.mail=

# Zona horaria
spring.jackson.time-zone=America/Bogota
spring.jackson.date-format=dd/MM/yyyy HH:mm:ss

# Token necesario para validación DNI
dni.security=

# Carpeta donde se guardarán los archivos de conductores
app.uploads.dir=uploads

# URL base pública del backend
app.base.url=http://localhost:8080
```

⚠️ Usa una **App Password de Google** (no tu contraseña normal).

---

## ▶️ Ejecución

Con Maven:

```bash
mvn spring-boot:run
```

El backend quedará disponible en:  
👉 [http://localhost:8080](http://localhost:8080)

---

## 📬 Test con Postman

En el proyecto se incluye el archivo **`RutasPrime.postman_collection.json`**, el cual contiene **todos los endpoints del sistema** listos para probar.

### Cómo usarlo:
1. Abre **Postman**.
2. Ve a **Importar** y selecciona `RutasPrime.postman_collection.json`.
3. Podrás probar de forma rápida:
    - Registro de usuario
    - Login
    - Verificación OTP
    - Recuperación de contraseña
    - Perfil protegido con JWT

---

## 📊 Sistema de Monitoreo con Prometheus y Grafana

Este proyecto implementa un sistema de monitoreo basado en Prometheus y Grafana para supervisar el rendimiento del backend, incluyendo métricas como uso de CPU, memoria, consumo del heap, peticiones HTTP, latencias y estado general del servicio.

### ⚙️ 1. Requisitos Previos
Antes de iniciar, asegúrate de tener instalado:

- Docker y Docker Compose (recomendado)
- Acceso al proyecto backend con micrómetro habilitado
- El backend debe contar con estas dependencias:
```properties
<!-- Micrometer + Prometheus -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>

<!-- Actuator -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```
- En el `application.properties`:
```properties
management.endpoints.web.exposure.include=prometheus,health,info,metrics
management.endpoint.prometheus.enabled=true
management.metrics.tags.application=backend-service
```

### 📦 2. Instalación de Prometheus y Grafana (Docker Compose)
- Crea un archivo `docker-compose.yml`
- Crea un archivo `prometheus.yml`

### ▶️ 3. Levantar el sistema de monitoreo
```bash
docker-compose up -d
```

### 📈 4. Configurar  / Crear Dashboard en Grafana
- Ingresa a Grafana:
```bash
http://localhost:3000
```
- Ir a Connections → Data Sources → Add Data Source
- Seleccionar Prometheus
- Configurar URL:
```bash
http://prometheus:9090
```
- En Grafana: Create → Import
- Seleccionar la fuente de datos Prometheus
- Métricas que podrás ver:
    - Uso de CPU del backend
    - Consumo de RAM y Heap
    - GC (Garbage Collector)
    - Promedio de latencia por endpoint 
    - Número de peticiones por ruta 
    - Errores 4xx / 5xx 
    - Threads activos 
    - Tiempo de respuesta promedio

### 🛑 5. Detener monitoreo
```bash
docker-compose down
```
---

## 🌱 Comandos Git básicos

### Clonar el proyecto
```bash
git clone https://github.com/JeancarloMejia/SistemaRutasprime_backend.git
cd SistemaRutasprime_backend
```

### Crear una nueva rama
```bash
git checkout -b mi-rama
```

### Guardar cambios
```bash
git add .
git commit -m "Descripción de los cambios"
```

### Subir tu rama al remoto
```bash
git push origin mi-rama
```

### Unir ramas (merge)
Primero cambia a la rama principal:
```bash
git checkout main
git pull origin main
git merge mi-rama
git push origin main
```

---

## 🗄️ Base de Datos
- Motor: **MySQL**
- ORM: **Hibernate / JPA**
- Estrategia: `ddl-auto=update` para generar tablas automáticamente.

---

## 🛡️ Seguridad
- Autenticación basada en **JWT**.
- Contraseñas protegidas con **BCrypt**.
- Filtros de seguridad con **Spring Security**.
- Control de acceso por roles.

---

## 💡 Notas finales
👉 [🎥 Video explicativo sobre las pruebas TDD aplicadas en el proyecto](https://www.youtube.com/watch?v=nliaTZWPBfA)

- El backend está diseñado para integrarse directamente con el **frontend de RutasPrime**.
- Para continuar con la interfaz de usuario, revisa el repo del frontend aquí:

👉 [🌐 SistemaRutasprime Frontend](https://github.com/P1erosebas8/SistemaRutasprime_frontend)
