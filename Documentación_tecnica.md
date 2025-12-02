
# Documentación Técnica – Turno Fácil

## 1. Información General

Turno Fácil es una aplicación móvil Android diseñada para gestionar turnos en establecimientos como bancos, consultorios médicos y oficinas públicas. Permite a los clientes solicitar y consultar turnos, y a los administradores gestionar el flujo de atención.

**Datos del proyecto:**

-   Nombre: Turno Fácil
    
-   Package: com.example.turnofacil
    
-   Versión: 1.0
    
-   Lenguajes usados: Kotlin, Java y PHP
    
-   Plataforma: Android
    
-   SDK mínimo: 24 (Android 7.0)
    
-   SDK objetivo: 36 (Android 14)

-   Backend: PHP 7.4+ con MySQL 5.7+
    

**Propósito:**  
Facilitar la administración de turnos, mejorar la organización y ofrecer una experiencia de usuario simple y eficiente.

---------
## 2. Arquitectura del Sistema

La aplicación utiliza una arquitectura basada en Activities con integración a backend PHP REST API, adecuada para proyectos Android de mediano alcance.

**Diagrama general:**

```
MainActivity
 ├── LoginActivity (autenticación)
 │   ├── CustomerActivity (cliente autenticado)
 │   └── AdminActivity (admin autenticado)
 └── CustomerWelcomeActivity (cliente sin registro)
 
Backend PHP REST API
 ├── /auth/login.php
 ├── /turnos/create.php
 ├── /turnos/list.php
 ├── /turnos/list_user.php
 ├── /turnos/update.php
 └── /turnos/stats.php
```

### Componentes de la arquitectura

1.  **Presentación (UI)**
    
    -   Activities para cada pantalla
        
    -   ConstraintLayout como base de diseño
        
    -   Uso de RecyclerView y CardView
        
    -   Recursos multi-idioma (values y values-en)
        
2.  **Modelo de datos**
    
    -   Data class Turn
        
    -   Entidades simples y serializables
        
3.  **Adaptadores**
    
    -   TurnAdapter para la lista de turnos en CustomerActivity
    
    -   TurnHistoryAdapter para historial de turnos
        
4.  **Lógica de negocio**
    
    -   Autenticación mediante API REST
        
    -   Gestión de turnos con backend PHP
        
    -   Estados del turno (pendiente, completado, cancelado)
        
    -   Navegación entre pantallas
        
    -   Cambio de idioma
        
5.  **Capa de Comunicación**
    
    -   ApiClient: Cliente HTTP para comunicación con backend
    
    -   Kotlin Coroutines para operaciones asíncronas
    
    -   Endpoints REST para turnos y autenticación
        
6.  **Backend PHP**
    
    -   API REST con PHP y MySQL
    
    -   Autenticación de usuarios
    
    -   CRUD de turnos
    
    -   Estadísticas en tiempo real
        
7.  **Capa Base**
    
    -   BaseActivity para comportamiento común (botón atrás)
        

----------

## 3. Requisitos del Sistema

### Requisitos de hardware

-   Dispositivo Android
    
-   Mínimo 2GB de RAM
    
-   Al menos 50MB de almacenamiento libre
    

### Requisitos de software

-   Android 7.0 o superior
    
-   Android SDK 24 a 36
    
-   Java 11 o superior
    
-   Kotlin 1.9+
    
-   Gradle 8.x
    
-   Android Studio Hedgehog o superior
    

----------

## 4. Estructura del Proyecto

```
redes/
├── app/
│   ├── src/main/java/com/example/turnofacil/
│   │   ├── MainActivity.kt
│   │   ├── BaseActivity.kt
│   │   ├── LoginActivity.kt
│   │   ├── AdminActivity.kt (migrado de Java a Kotlin)
│   │   ├── CustomerActivity.kt
│   │   ├── CustomerWelcomeActivity.kt
│   │   ├── ApiClient.kt (cliente HTTP)
│   │   ├── Turn.kt
│   │   ├── TurnAdapter.kt
│   │   └── TurnHistoryAdapter.kt
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_login.xml
│   │   │   ├── activity_customer_welcome.xml
│   │   │   └── item_turn_history.xml
│   │   ├── values/
│   │   ├── values-en/
│   │   ├── drawable/
│   │   └── mipmap-*/
│   └── AndroidManifest.xml
├── api/
│   ├── auth/
│   │   └── login.php
│   ├── config/
│   │   └── database.php
│   └── turnos/
│       ├── create.php
│       ├── list.php
│       ├── list_user.php
│       ├── update.php
│       └── stats.php
├── config/
│   ├── config.php
│   └── index.php
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
├── schema-bd.md
├── setup_database.sql
├── API_DOCUMENTATION.md
└── README.md
```

----------

## 5. Componentes Principales

### 5.1 MainActivity

Pantalla principal de la aplicación. Desde aquí el usuario puede seleccionar su tipo de acceso y cambiar el idioma del sistema.

**Funciones principales:**

-   Navegación a LoginActivity (para usuarios registrados)
    
-   Navegación a CustomerWelcomeActivity (para tomar turno sin registro)
    
-   Cambio dinámico de idioma mediante setLocale()

**Cambios recientes:**
- Se agregó flujo de autenticación
- Separación entre usuarios registrados y no registrados
    

----------

### 5.2 BaseActivity

Clase abstracta utilizada por algunas Activities para unificar el comportamiento del botón "Atrás" en la ActionBar.

----------

### 5.3 LoginActivity

Pantalla de autenticación para usuarios registrados (clientes y administradores).

**Funciones:**

-   Validación de credenciales mediante API REST
    
-   Autenticación con email y contraseña
    
-   Redirección automática según rol (admin o cliente)
    
-   Manejo de errores de conexión
    
-   Feedback visual durante el proceso de login

**Implementación técnica:**
```kotlin
// Conexión con backend PHP
val jsonBody = JSONObject().apply {
    put("email", email)
    put("password", password)
}
val response = ApiClient.post("/auth/login.php", jsonBody)
```

----------

### 5.4 AdminActivity

Pantalla destinada a los administradores del sistema.

**Funciones:**

-   Gestionar turnos activos mediante API REST
    
-   Ver estadísticas en tiempo real (en espera, atendidos, cancelados)
    
-   Llamar al siguiente turno
    
-   Cancelar turnos
    
-   Actualización automática de datos desde el servidor
    
-   Interfaz basada en Material Design
    

**Cambios recientes:**
- Migrado de Java a Kotlin
- Integración con backend PHP
- Obtención de datos en tiempo real desde MySQL

----------

### 5.5 CustomerWelcomeActivity

Pantalla para clientes que desean tomar turno sin registro previo.

**Funciones:**

-   Solicitar nombre y cédula
    
-   Generar turno automáticamente
    
-   Crear usuario temporal con credenciales automáticas
    
-   Mostrar número de turno asignado
    
-   Consultar posición en la cola

**Flujo de creación:**
- Email: `[cedula]@turno.facil.com`
- Password: mismo valor que la cédula
- Rol: automáticamente `cliente`

----------

### 5.6 CustomerActivity

Pantalla destinada a los clientes autenticados que consultan sus turnos.

**Funciones:**

-   Mostrar lista de turnos personales mediante RecyclerView
    
-   Ver turnos desde backend (históricos y actuales)
    
-   Mostrar turno en atención actual
    
-   Mostrar posición en la cola
    
-   Destacar visualmente los turnos en espera y el turno actual
    
-   Actualización en tiempo real desde API REST
    

----------

### 5.7 Turn (Modelo)

Clase de datos que representa un turno dentro de la aplicación.

```kotlin
data class Turn(
    val id: Int,
    val turnNumber: String,
    val status: String,
    val isAttending: Boolean = false,
    val fecha_creacion: String? = null,
    val actualizado_en: String? = null
)
```

----------

### 5.8 TurnAdapter

Adaptador utilizado para mostrar la lista de turnos en CustomerActivity.

**Responsabilidades:**

-   Inflar elementos de lista
    
-   Asignar datos de Turn a cada vista
    
-   Aplicar estilos visuales según el estado del turno
    
-   Gestionar eventos de click en items

----------

### 5.9 TurnHistoryAdapter

Adaptador para mostrar el historial de turnos del usuario.

**Responsabilidades:**

-   Mostrar turnos completados y cancelados
    
-   Formato de fechas
    
-   Indicadores visuales de estado

----------

### 5.10 ApiClient

Clase singleton para comunicación HTTP con el backend PHP.

**Funciones:**

-   Métodos POST, GET y PUT
    
-   Manejo de JSON para request/response
    
-   Gestión de errores de conexión
    
-   Logging de peticiones
    
-   Uso de Coroutines para operaciones asíncronas

**Configuración:**
```kotlin
private const val BASE_URL = "http://10.0.2.2/redes/api" // Emulador
// Para dispositivo real: "http://192.168.x.x/redes/api"
```
    

----------

## 6. Flujo de Navegación

```
MainActivity
 ├── "Tomar Turno" → CustomerWelcomeActivity → Generar turno sin registro
 └── "Ingresar" → LoginActivity
      ├── Usuario con rol "admin" → AdminActivity
      │    └── Gestión de turnos y estadísticas
      └── Usuario con rol "cliente" → CustomerActivity
           └── Consulta de turnos personales
```

**Descripción detallada:**

1.  **Inicio de la aplicación:**
    -   La app inicia en MainActivity
    -   Usuario elige entre "Tomar Turno" (sin registro) o "Ingresar" (con autenticación)

2.  **Flujo sin registro (CustomerWelcomeActivity):**
    -   Ingresa nombre y cédula
    -   El sistema crea usuario automático
    -   Se genera turno y muestra número asignado
    -   Puede consultar posición en cola

3.  **Flujo con autenticación (LoginActivity):**
    -   Usuario ingresa email y contraseña
    -   API valida credenciales contra MySQL
    -   Redirección automática según rol:
        - **Admin**: AdminActivity (gestión completa)
        - **Cliente**: CustomerActivity (consulta personal)

4.  **Panel de Administrador (AdminActivity):**
    -   Ve todos los turnos en tiempo real
    -   Puede llamar siguiente turno
    -   Puede cancelar turnos
    -   Ve estadísticas actualizadas
    -   Datos desde backend PHP

5.  **Panel de Cliente (CustomerActivity):**
    -   Ve sus turnos personales
    -   Ve turno en atención actual
    -   Consulta su posición en cola
    -   Historial de turnos anteriores
    
6.  **Botón Atrás:**
    -   Todas las pantallas secundarias permiten regresar
    

----------

## 7. Backend API REST (PHP)

La aplicación cuenta con un backend desarrollado en PHP que proporciona servicios REST para la gestión de turnos y autenticación.

### 7.1 Configuración del Backend

**Requisitos:**
- XAMPP (Apache + MySQL + PHP 7.4+)
- MySQL 5.7 o superior
- Puerto 80 disponible para Apache

**Instalación:**
1. Instalar XAMPP
2. Copiar carpeta `api/` en `c:\xampp\htdocs\redes\`
3. Importar `setup_database.sql` en phpMyAdmin
4. Configurar credenciales en `api/config/database.php`
5. Iniciar Apache y MySQL desde XAMPP Control Panel

**URL Base:**
- Local: `http://localhost/redes/api/`
- Emulador Android: `http://10.0.2.2/redes/api/`
- Dispositivo real: `http://[TU_IP_LOCAL]/redes/api/`

### 7.2 Endpoints Disponibles

#### Autenticación

**POST** `/auth/login.php`

Autentica usuarios (admin o cliente) y retorna datos del usuario.

**Request:**
```json
{
  "email": "admin@turnofacil.com",
  "password": "admin123"
}
```

**Response exitosa:**
```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "id": 1,
    "nombre": "Administrador",
    "email": "admin@turnofacil.com",
    "rol": "admin"
  }
}
```

#### Gestión de Turnos

**POST** `/turnos/create.php` - Crear nuevo turno

**GET** `/turnos/list.php?estado=pendiente&fecha=2025-11-30` - Listar turnos

**GET** `/turnos/list_user.php?id_usuario=1` - Turnos de usuario específico

**PUT** `/turnos/update.php` - Actualizar estado de turno

**GET** `/turnos/stats.php?fecha=2025-11-30` - Estadísticas del día

### 7.3 Credenciales de Prueba

**Administrador:**
- Email: `admin@turnofacil.com`
- Password: `admin123`
- Rol: `admin`

**Clientes de prueba:**
- Email: `juan.perez@email.com` / Password: `pass123`
- Email: `maria.garcia@email.com` / Password: `pass123`
- Email: `carlos.lopez@email.com` / Password: `pass123`
- Rol: `cliente`

**Clientes automáticos:**
Al tomar turno sin registro, se crea automáticamente:
- Email: `[cedula]@turno.facil.com`
- Password: valor de la cédula
- Rol: `cliente`

### 7.4 Estructura del Backend

```
api/
├── auth/
│   └── login.php           # Autenticación de usuarios
├── config/
│   └── database.php        # Conexión a MySQL
└── turnos/
    ├── create.php          # Crear turno
    ├── list.php            # Listar todos los turnos
    ├── list_user.php       # Turnos de un usuario
    ├── update.php          # Actualizar estado
    └── stats.php           # Estadísticas
```

Para documentación completa de la API, ver: `API_DOCUMENTATION.md`

----------

## 8. Modelo de Base de Datos

La aplicación utiliza una base de datos relacional compuesta por cuatro tablas principales: usuarios, turnos, notificaciones y sucursales.

**Base de datos:** turno_facil

### 7.1 Tabla usuarios

| Campo           | Tipo                    | Restricciones | Descripción              |
|-----------------|-------------------------|--|--------------------------|
| id              | INT                     | PK, AI | Identificador único      |
| id_sucursal     | INT                     | FK, NOT NULL | Sucursal asignada (referencia a sucursales) |
| nombre          | VARCHAR(100)            | NOT NULL | Nombre del usuario       |
| email           | VARCHAR(255)            | UNIQUE, NOT NULL | Correo único             |
| password        | VARCHAR(255)            | NOT NULL | Hash de contraseña       |
| rol             | ENUM(cliente, admin)    | DEFAULT 'cliente' | Rol del usuario          |
| fecha_registro  | DATETIME                | DEFAULT CURRENT_TIMESTAMP | Fecha de registro        |

----------

### 7.2 Tabla turnos

| Campo          | Tipo                                      | Restricciones | Descripción               |
|----------------|-------------------------------------------|--|---------------------------|
| id             | INT                                        | PK, AI | ID del turno              |
| id_usuario     | INT                                        | FK, NOT NULL | Usuario dueño del turno (referencia a usuarios) |
| id_sucursal    | INT                                        | FK, NOT NULL | Sucursal del turno (referencia a sucursales) |
| numero_turno   | VARCHAR(50)                                | NOT NULL | Código del turno (A01, B02, etc.) |
| estado         | ENUM(pendiente, cancelado, completado)     | DEFAULT 'pendiente' | Estado del turno          |
| fecha_creacion | DATETIME                                   | DEFAULT CURRENT_TIMESTAMP | Fecha de creación         |
| actualizado_en | DATETIME                                   | DEFAULT CURRENT_TIMESTAMP ON UPDATE | Última actualización      |
----------

### 7.3 Tabla notificaciones

| Campo        | Tipo              | Restricciones | Descripción                  |
|--------------|-------------------|--|------------------------------|
| id           | INT               | PK, AI | ID de la notificación        |
| id_usuario   | INT               | FK, NOT NULL | Usuario destinatario (referencia a usuarios) |
| titulo       | VARCHAR(100)      | NOT NULL | Título del mensaje           |
| mensaje      | VARCHAR(255)      | NOT NULL | Contenido del mensaje        |
| leida        | BOOLEAN           | DEFAULT 0 | Indicador de lectura (0=no, 1=sí) |
| fecha_envio  | DATETIME          | DEFAULT CURRENT_TIMESTAMP | Fecha de envío               |

----------

### 7.4 Tabla sucursales

| Campo       | Tipo              | Restricciones | Descripción                  |
|-------------|-------------------|--|------------------------------|
| id_sucursal | INT               | PK, AI | Identificador único          |
| nombre      | VARCHAR(120)      | NOT NULL | Nombre de la sucursal        |
| ubicacion   | VARCHAR(255)      | NOT NULL | Dirección                    |
| telefono    | VARCHAR(20)       | Nullable | Teléfono de contacto         |

----------

### 7.5 Relaciones y Restricciones

**Relaciones:**
```
sucursales (1) <--> (N) usuarios
sucursales (1) <--> (N) turnos
usuarios (1) <--> (N) turnos
usuarios (1) <--> (N) notificaciones
```

**Integridad referencial:**
- usuarios.id_sucursal → sucursales.id_sucursal (ON UPDATE CASCADE, ON DELETE CASCADE)
- turnos.id_usuario → usuarios.id (ON UPDATE CASCADE, ON DELETE CASCADE)
- turnos.id_sucursal → sucursales.id_sucursal (ON UPDATE CASCADE, ON DELETE CASCADE)
- notificaciones.id_usuario → usuarios.id (ON UPDATE CASCADE, ON DELETE CASCADE)

**Diagrama Entidad-Relación (ER):**

```
┌──────────────────────┐
│    sucursales        │
├──────────────────────┤
│ id_sucursal (PK)     │◄────┐
│ nombre               │     │
│ ubicacion            │     │
│ telefono             │     │
└──────────────────────┘     │
         ▲                   │
         │                   │
    (1)──┼───(N)             │
         │                   │
    ┌────┴───────────────────┴────┐
    │                             │
┌───┴──────────────────┐    ┌─────┴───────────────┐
│     usuarios         │    │      turnos         │
├──────────────────────┤    ├─────────────────────┤
│ id (PK)              │    │ id (PK)             │
│ id_sucursal (FK)─────┼────┤ id_usuario (FK)     │
│ nombre               │    │ id_sucursal (FK)────┤
│ email (UNIQUE)       │    │ numero_turno        │
│ password             │    │ estado              │
│ rol                  │    │ fecha_creacion      │
│ fecha_registro       │    │ actualizado_en      │
└──────┬───────────────┘    └─────────────────────┘
       │
       │ (1)──(N)
       │
    ┌──┴──────────────────┐
    │  notificaciones     │
    ├─────────────────────┤
    │ id (PK)             │
    │ id_usuario (FK)─────┤
    │ titulo              │
    │ mensaje             │
    │ leida               │
    │ fecha_envio         │
    └─────────────────────┘
```

----------

### 7.6 Datos de Prueba Incluidos

El esquema incluye datos de prueba para facilitar el desarrollo y testing:

**Sucursales (3 registros):**
- Costa Verde (La Chorrera, Panamá Oeste) - 507-800-0001 [id: 1]
- Panamá (Ave. Balboa, Ciudad de Panamá) - 507-800-0002 [id: 2]
- Chiriquí (David, Provincia de Chiriquí) - 507-800-0003 [id: 3]

**Usuarios (5 registros):**
- Administrador (sucursal 1): admin@turnofacil.com [rol: admin]
- Juan Pérez (sucursal 1): juan.perez@email.com [rol: cliente]
- María García (sucursal 2): maria.garcia@email.com [rol: cliente]
- Carlos López (sucursal 3): carlos.lopez@email.com [rol: cliente]
- Ana Martínez (sucursal 2): ana.martinez@email.com [rol: cliente]

**Turnos (5 registros):**
- A12 (usuario 2, sucursal 1) - pendiente
- B05 (usuario 3, sucursal 2) - pendiente
- C18 (usuario 4, sucursal 3) - completado
- A25 (usuario 5, sucursal 2) - pendiente
- D09 (usuario 2, sucursal 1) - cancelado----------

### 7.7 Orden de Creación de Tablas

Es importante respetar el siguiente orden para evitar errores de integridad referencial:

1. **sucursales** - Sin dependencias
2. **usuarios** - Depende de sucursales
3. **turnos** - Depende de usuarios y sucursales
4. **notificaciones** - Depende de usuarios

### 7.8 Notas de Implementación

- **Integridad referencial:** Todas las relaciones utilizan `ON UPDATE CASCADE` y `ON DELETE CASCADE`
- **números de turno:** Campo `VARCHAR(50)` permite números con letras (A01, B05, etc.)
- **Sucursales:** Se crean primero en los inserts de prueba
- **Usuarios:** Cada usuario está asignado a una sucursal específica
- **Turnos:** Registran tanto el usuario como la sucursal para facilitar consultas y reportes

----------

## 9. Características Implementadas

### Frontend (Android)

-   ✅ Navegación mediante Intents
    
-   ✅ Botón atrás funcional
    
-   ✅ Diseño Material Design
    
-   ✅ Soporte multi-idioma (español e inglés)
    
-   ✅ RecyclerView con adaptadores personalizados
    
-   ✅ Vista mejorada para turno en atención
    
-   ✅ Estructura del proyecto clara y modular
    
-   ✅ Uso de data classes y ViewHolder Pattern

-   ✅ Sistema de autenticación con backend

-   ✅ Cliente HTTP con ApiClient (Coroutines)

-   ✅ Gestión de turnos en tiempo real

-   ✅ Actualización automática desde API REST

-   ✅ Manejo de errores de conexión

-   ✅ Feedback visual con Toast messages

### Backend (PHP + MySQL)

-   ✅ API REST completa con 7 endpoints

-   ✅ Autenticación de usuarios

-   ✅ CRUD de turnos

-   ✅ Estadísticas en tiempo real

-   ✅ Gestión de roles (admin/cliente)

-   ✅ Creación automática de usuarios temporales

-   ✅ Consultas optimizadas con prepared statements

-   ✅ Respuestas JSON estandarizadas

-   ✅ Manejo de errores y validaciones
    

----------

## 10. Configuración del Proyecto

### build.gradle.kts (resumen)

```
android {
    namespace = "com.example.turnofacil"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.turnofacil"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

```

### Dependencias principales

-   androidx.core:core-ktx
    
-   androidx.appcompat
    
-   material
    
-   constraintlayout
    
-   recyclerview
    
-   cardview

-   **kotlinx-coroutines-android:1.7.3** (nuevo - para operaciones asíncronas)
    

----------

## 11. Guía de Compilación

### Requisitos previos

**Para Android:**
-   Instalar Android Studio
    
-   Instalar JDK 11
    
-   Tener las SDK necesarias (API 24–36)

**Para Backend:**
-   XAMPP instalado (Apache + MySQL + PHP)

-   Puerto 80 disponible

-   phpMyAdmin para gestionar base de datos
    

### Pasos

**1. Configurar el Backend:**

a. Instalar XAMPP y ejecutarlo

b. Copiar carpeta `api/` a `c:\xampp\htdocs\redes\`

c. Abrir phpMyAdmin: `http://localhost/phpmyadmin`

d. Crear base de datos: Importar `setup_database.sql`

e. Verificar que Apache y MySQL estén corriendo

f. Probar endpoint: `http://localhost/redes/api/turnos/stats.php`

**2. Configurar la App Android:**

a. Clonar el repositorio
    
```bash
git clone https://github.com/Giancarlo174/redes.git
cd redes
git checkout dev
```
    
b. Abrir en Android Studio
    
c. Sincronizar Gradle

d. Configurar URL en `ApiClient.kt`:
   - Emulador: `http://10.0.2.2/redes/api`
   - Dispositivo real: `http://[TU_IP]/redes/api`

e. Ejecutar la app en dispositivo o emulador

**3. Verificar conexión:**

- Login con: `admin@turnofacil.com` / `admin123`
- Si hay error de conexión, verificar que Apache esté corriendo
- Verificar firewall si usas dispositivo real
    

### Generar APK

```
./gradlew assembleDebug
./gradlew assembleRelease

```

----------

## 11. Internacionalización

Se soportan dos idiomas: español e inglés.

Archivos involucrados:

-   res/values/strings.xml (ES)
    
-   res/values-en/strings.xml (EN)
    

Cambio dinámico de idioma mediante:

```
private fun setLocale(languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = Configuration()
    config.setLocale(locale)
    baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    recreate()
}

```

----------

## 13. Mejoras Futuras

### Backend

-   ~~Implementación de API REST~~ ✅ Completado
    
-   Autenticación con JWT (actualmente basic auth)
    
-   ~~Persistencia real en MySQL~~ ✅ Completado

-   Implementar tokens de sesión

-   Rate limiting para prevenir abuso

-   Logs de auditoría
    

### Funcionalidad de negocio

-   Algoritmo automático de asignación de turnos
    
-   Notificaciones push
    
-   Gestión de prioridades
    
-   Estadísticas avanzadas
    

### Arquitectura

-   Migración a MVVM
    
-   Uso de ViewModel y LiveData
    
-   Repository Pattern
    
-   Navigation Component
    

### UI/UX

-   Animaciones
    
-   Modo oscuro completo
    
-   Accesibilidad mejorada
    

### Testing

-   Tests unitarios
    
-   Tests de integración
    
-   Tests UI con Espresso
    

----------

## 13. Mantenimiento y Soporte

Comandos útiles:

```
./gradlew clean
./gradlew test
./gradlew dependencies
./gradlew dependencyUpdates

```

Herramientas de depuración:

-   Logcat
    
-   Android Profiler
    
-   Layout Inspector
    

Versionado:

-   Se utiliza SemVer (MAJOR.MINOR.PATCH)
    

----------

## 14. Contacto y Contribuciones

-   Repositorio: redes
    
-   Owner: Giancarlo174
    
-   Rama principal: main
    

Pasos para contribuir:

1.  Crear un fork
    
2.  Crear una nueva rama
    
3.  Realizar cambios y hacer commit
    
4.  Enviar pull request
    

----------

## 15. Glosario

-   Activity: Pantalla de interfaz
    
-   Intent: Objeto para navegación
    
-   Layout: Estructura UI en XML
    
-   Gradle: Sistema de construcción
    
-   RecyclerView: Lista eficiente
    
-   ViewHolder: Patrón de optimización de listas
    
-   PK/FK: Llaves primarias y foráneas
    
-   ORM: Mapeo objeto-relacional
    
-   JWT: Token de autenticación
    
-   MVVM: Patrón arquitectónico
    
-   ConstraintLayout: Layout flexible en Android
    

----------

## 16. Historial de Versiones

| Versión | Fecha       | Rama | Cambios                                                      |
|---------|-------------|------|--------------------------------------------------------------|
| 2.0     | 30/11/2025  | dev  | **Backend completo:** API REST PHP, autenticación, LoginActivity, CustomerWelcomeActivity, ApiClient, Coroutines, integración MySQL, 7 endpoints REST, migración AdminActivity a Kotlin |
| 1.2     | 17/11/2025  | main | Implementación de RecyclerView, TurnAdapter, modelo Turn, migración de AdminActivity a Java, tabla sucursales |
| 1.1     | 11/11/2025  | main | Definición del modelo completo de base de datos (4 tablas)  |
| 1.0     | 11/11/2025  | main | Versión inicial del proyecto                                 |

**Nota:** La versión 2.0 está en la rama `dev` y contiene la integración completa con backend PHP + MySQL.

