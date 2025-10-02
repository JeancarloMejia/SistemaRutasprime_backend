# 🌐 SistemaRutasprime Backend

Este proyecto es el **backend** del sistema **RutasPrime**, encargado de la gestión de usuarios, autenticación, verificación vía OTP y recuperación de contraseñas.  

Construido con **Spring Boot 3.5.6**, **Java 17**, **Spring Security**, **JWT**, **Hibernate/JPA** y **MySQL**.  
Incluye soporte de **notificaciones por correo** usando Gmail SMTP y **plantillas HTML** personalizadas.

---

## 📖 Tabla de Contenidos

- [✨ Características](#-características)  
- [⚙️ Dependencias](#-dependencias)  
- [📂 Estructura del Proyecto](#-estructura-del-proyecto)  
- [🛠️ Configuración](#-configuración)  
- [▶️ Ejecución](#-ejecución)  
- [📬 Test con Postman](#-test-con-postman)  
- [🌱 Comandos Git básicos](#-comandos-git-básicos)  
- [🗄️ Base de Datos](#-base-de-datos)  
- [🛡️ Seguridad](#-seguridad)  
- [💡 Notas finales](#-notas-finales)  

---

## ✨ Características

- 🔑 **Registro y login** de usuarios.  
- 🔒 **Contraseñas encriptadas** con `BCryptPasswordEncoder`.  
- 📧 **OTP (One Time Password)** para validación y recuperación de contraseñas.  
- 📨 **Envío de correos HTML** con plantillas (`otp-register.html`, `otp-reset.html`, `welcome.html`).  
- 👤 **Perfil de usuario protegido** por JWT.  
- 🛡️ **Spring Security + Filtros JWT** para autenticación y autorización.  
- 🗄️ **JPA/Hibernate** para persistencia en base de datos MySQL.  

---

## ⚙️ Dependencias

Definidas en `pom.xml`:  

- **Spring Boot Starter Web** → Construcción de REST API  
- **Spring Boot Starter Data JPA** → Persistencia con Hibernate  
- **MySQL Connector** → Conexión a base de datos MySQL  
- **Spring Boot Starter Security** → Seguridad y autenticación  
- **JJWT (io.jsonwebtoken)** → Manejo de tokens JWT  
- **Spring Boot Starter Mail** → Envío de correos electrónicos  
- **Lombok** → Reducir boilerplate en clases Java  
- **Spring Boot Starter Test** → Pruebas unitarias  

---

## 📂 Estructura del Proyecto

```bash
src/main/java/com/backend/avance1/
├── config/
│   └── SecurityConfig.java                # Configuración de seguridad y CORS
│
├── controller/
│   ├── AuthController.java                # Endpoints de autenticación (login, registro, OTP)
│   └── UserController.java                # Endpoints de perfil de usuario
│
├── dto/                                   # Data Transfer Objects
│   ├── ApiResponse.java
│   ├── ChangePasswordDTO.java
│   ├── ResetPasswordDTO.java
│   ├── UpdateUserDTO.java
│   └── UserDTO.java
│
├── entity/
│   ├── Otp.java                           # Entidad OTP
│   └── User.java                          # Entidad Usuario
│
├── exception/
│   └── GlobalExceptionHandler.java        # Manejo global de excepciones
│
├── repository/
│   ├── OtpRepository.java
│   └── UserRepository.java
│
├── security/
│   ├── JwtAuthEntryPoint.java             # Manejo de errores de autenticación
│   ├── JwtAuthFilter.java                 # Filtro JWT para requests
│   └── JwtUtil.java                       # Utilidades para JWT
│
├── service/
│   ├── MailService.java                   # Envío de correos con SMTP
│   ├── OtpService.java                    # Lógica de OTP
│   └── UserService.java                   # Lógica de negocio de usuarios
│
├── util/
│   └── Avance1Application.java            # Clase principal de Spring Boot
│
resources/
├── static/
│   └── logo.jpg                           # Logo estático usado en correos o frontend
│
├── templates/                             # Plantillas HTML para correos
│   ├── otp-register.html                  # Correo de verificación OTP
│   ├── otp-reset.html                     # Correo de recuperación OTP
│   ├── password-changed.html              # Notificación de cambio de contraseña
│   ├── profile-updated.html               # Notificación de actualización de perfil
│   └── welcome.html                       # Correo de bienvenida
│
├── application.properties                 # Configuración de la app
└── data.sql                               # Datos iniciales para la BD (opcional)

test/                                      # Pruebas unitarias (vacío o en construcción)
```

---

## 🛠️ Configuración

### 1. Base de datos
Crea una base de datos vacía, por ejemplo:  

```sql
CREATE DATABASE rutasprime_db;
```

👉 No es necesario crear tablas: **Hibernate las generará automáticamente**.  

### 2. Archivo `application.properties`
Configura tus credenciales de MySQL y correo en:

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
- El backend está diseñado para integrarse directamente con el **frontend de RutasPrime**.  
- Para continuar con la interfaz de usuario, revisa el repo del frontend aquí:  

👉 [🌐 SistemaRutasprime Frontend](https://github.com/P1erosebas8/SistemaRutasprime_frontend)
