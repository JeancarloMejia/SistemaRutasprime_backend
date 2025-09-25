# 🌐 SistemaRutasprime Backend

Este proyecto es el **backend** de un sistema de gestión de usuarios con autenticación, verificación vía OTP y recuperación de contraseñas.
Construido con **Spring Boot 3.5.6**, **Java 17**, **Spring Security**, **JWT**, **Hibernate/JPA** y **MySQL**.

Incluye soporte de **notificaciones por correo** usando Gmail SMTP y **plantillas HTML** personalizadas.

---

## 📖 Tabla de Contenidos

* [✨ Características](#-características)
* [⚙️ Dependencias](#-dependencias)
* [📂 Estructura del Proyecto](#-estructura-del-proyecto)
* [🛠️ Configuración](#-configuración)
* [▶️ Ejecución](#-ejecución)

  * [Registro](#registro)
  * [Verificación OTP](#verificación-otp)
  * [Login](#login)
  * [Perfil](#perfil)
  * [Olvido y reseteo de contraseña](#olvido-y-reseteo-de-contraseña)
* [📧 Flujo de Correos OTP](#-flujo-de-correos-otp)
* [🗄️ Base de Datos](#-base-de-datos)
* [🛡️ Seguridad](#-seguridad)
* [💡 Notas finales](#-notas-finales)

---

## ✨ Características

* 🔑 **Registro y login** de usuarios.
* 🔒 **Contraseñas encriptadas** con `BCryptPasswordEncoder`.
* 📧 **OTP (One Time Password)** para validación y recuperación de contraseñas.
* 📨 **Envío de correos HTML** con plantillas (`otp-register.html`, `otp-reset.html`).
* 👤 **Perfil de usuario protegido** por JWT.
* 🛡️ **Spring Security + Filtros JWT** para autenticación y autorización.
* 🗄️ **JPA/Hibernate** para persistencia en base de datos MySQL.

---

## ⚙️ Dependencias

Definidas en `pom.xml`:

* **Spring Boot Starter Web** → Construcción de REST API
* **Spring Boot Starter Data JPA** → Persistencia con Hibernate
* **MySQL Connector** → Conexión a base de datos MySQL
* **Spring Boot Starter Security** → Seguridad y autenticación
* **JJWT (io.jsonwebtoken)** → Manejo de tokens JWT
* **Spring Boot Starter Mail** → Envío de correos electrónicos
* **Lombok** → Reducir boilerplate en clases Java
* **Spring Boot Starter Test** → Pruebas unitarias

---

## 📂 Estructura del Proyecto

```
src/main/java/com/backend/avance1/
├── config/
│   ├── CorsConfig.java
│   └── SecurityConfig.java
│
├── controller/
│   ├── AuthController.java       # Endpoints de autenticación
│   └── UserController.java       # Endpoint de perfil
│
├── dto/
│   └── (objetos de transferencia de datos)
│
├── entity/
│   ├── User.java                 # Entidad usuario
│   └── Otp.java                  # Entidad OTP
│
├── repository/
│   ├── UserRepository.java
│   └── OtpRepository.java
│
├── security/
│   ├── JwtAuthFilter.java
│   └── JwtUtil.java
│
├── service/
│   ├── UserService.java
│   ├── OtpService.java
│   └── MailService.java
│
├── util/
│   └── Avance1Application.java
│
resources/
├── static/
│   └── logo.jpg
├── templates/
│   ├── otp-register.html         # Correo de registro
│   └── otp-reset.html            # Correo de recuperación
└── application.properties
```

---

## 🛠️ Configuración

### 1. Base de datos

Crea una base de datos vacía (ejemplo: `rutasprime_db`).
👉 No necesitas crear tablas: **Hibernate las generará automáticamente**.

### 2. Archivo `application.properties`

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/rutasprime_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Mail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu_correo@gmail.com
spring.mail.password=tu_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Server
server.port=8080
```

⚠️ Usa una **App Password** de Google, no tu contraseña normal.

---

## ▶️ Ejecución

Con Maven:

```bash
mvn spring-boot:run
```

El servidor estará disponible en:

```
http://localhost:8080
```