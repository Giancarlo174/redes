
# Documentación Técnica – Turno Fácil

## 1. Información General

Turno Fácil es una aplicación móvil Android diseñada para gestionar turnos en establecimientos como bancos, consultorios médicos y oficinas públicas. Permite a los clientes solicitar y consultar turnos, y a los administradores gestionar el flujo de atención.

**Datos del proyecto:**

-   Nombre: Turno Fácil
    
-   Package: com.example.turnofacil
    
-   Versión: 2.1
    
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

| Componente | Descripción | Características v2.1 |
|---|---|---|
| **MainActivity** | Pantalla inicial de navegación | Selecciona Login/Welcome, cambio de idioma (setLocale) |
| **BaseActivity** | Clase base para Activities | Maneja comportamiento común del botón atrás |
| **LoginActivity** | Autenticación (email+password) | Valida credenciales contra API, redirección por rol |
| **AdminActivity** (Kotlin) | Panel admin para gestores | **Spinner** sucursales, **estadísticas dinámicas**, auto-refresh Handler(3s) |
| **CustomerWelcomeActivity** | Generador turno sin registro | Nombre+Cédula → usuario automático; **v2.1:** selector sucursal, validación pendiente |
| **CustomerActivity** | Panel cliente autenticado | Muestra turnos personales; **v2.1:** auto-refresh Handler(5s), filtro sucursal |
| **Turn** | Data class | id, turnNumber, status, isAttending, fechas |
| **TurnAdapter** | RecyclerView adapter | Infla items, estilos por estado, maneja clicks |
| **TurnHistoryAdapter** | Adapter historial | Muestra completados/cancelados con fechas |
| **ApiClient** | Singleton HTTP cliente | POST/GET/PUT, JSON handling, Coroutines, logging |

**ApiClient URL:** `http://10.0.2.2/redes/api` (emulador), `http://localhost/redes/api` (local), `http://[IP]/redes/api` (dispositivo real)
    

----------

## 6. Flujo de Navegación

```
MainActivity
 ├─ "Tomar Turno" → CustomerWelcomeActivity (sin registro)
 └─ "Ingresar" → LoginActivity → Admin/Cliente
      ├─ AdminActivity (todos los turnos, estadísticas, filtro sucursal)
      └─ CustomerActivity (turnos personales, historial)
```

| Flujo | Pasos | Resultado |
|---|---|---|
| **Sin registro** | Nombre+Cédula → SelectSucursal → ValidarPendiente | Crear usuario automático + Turno + Posición cola |
| **Con autenticación** | Email+Password → ApiValidate | Redirección por rol (admin/cliente) |
| **Admin** | LoginActivity → AdminActivity | Ve todos turnos (tiempo real), estadísticas dinámicas, filtro sucursal |
| **Cliente** | LoginActivity → CustomerActivity | Ve sus turnos, en atención actual, posición cola, historial |
    

----------

## 7. Backend API REST (PHP)

**Tech Stack:** PHP 7.4+, MySQL 5.7+, XAMPP (Apache+PHP+MySQL)

**Instalación rápida:**
1. XAMPP → C:\xampp\htdocs\redes\
2. Importar `setup_database.sql` en phpMyAdmin
3. URLs: Local `http://localhost/redes/api/` | Emulador `http://10.0.2.2/redes/api/` | Dispositivo real `http://[TU_IP]/redes/api/`

### 7.1 Endpoints (8 total) y Credenciales

| Endpoint | Método | Parámetros | v2.1 | User | Pass |
|---|---|---|---|---|---|
| `/auth/login.php` | POST | email, password | - | admin@turnofacil.com | admin123 |
| `/turnos/create.php` | POST | nombre, cedula, id_sucursal | ✨ Valida pendiente | juan.perez@email.com | pass123 |
| `/turnos/list.php` | GET | estado, fecha, id_sucursal | ✨ Filtro sucursal | maria.garcia@email.com | pass123 |
| `/turnos/list_user.php` | GET | id_usuario, id_sucursal | ✨ Filtro sucursal | carlos.lopez@email.com | pass123 |
| `/turnos/update.php` | PUT | turno_data | - | auto@turno.facil.com | cedula |
| `/turnos/stats.php` | GET | fecha, id_sucursal | ✨ Filtro sucursal | | |
| `/sucursales/list.php` | GET | - | ✨ NEW ENDPOINT | | |
| `/auth/logout.php` | POST | - | - | | |

**Estructura:** `api/auth/{login,logout}.php`, `api/config/database.php`, `api/sucursales/list.php`, `api/turnos/{create,list,list_user,update,stats}.php`

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

**Sucursales (4 registros):**
- Costa Verde (La Chorrera, Panamá Oeste) - 507-800-0001 [id: 1]
- Panamá (Ave. Balboa, Ciudad de Panamá) - 507-800-0002 [id: 2]
- Chiriquí (David, Provincia de Chiriquí) - 507-800-0003 [id: 3]
- **Todos** (Vista consolidada) - 507-800-0000 [id: 4] ✨ **NEW v2.1**

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

## 9. Características Implementadas

| Componente | Estado | Features v2.1 |
|---|---|---|
| **Frontend (Android)** | ✅ Completo | Navegación Intents, Material Design, multi-idioma, RecyclerView, Coroutines, autenticación, auto-refresh (3s admin/5s cliente), filtro sucursal |
| **Backend (PHP) - 8 endpoints** | ✅ Completo | API REST, MySQL, autenticación, CRUD turnos, estadísticas reales, prepared statements, JSON responses, validación pendiente, endpoint /sucursales/list.php |

---

## 10. Configuración e Instalación

**Requisitos:** Android Studio (JDK 11, SDK 24-36), XAMPP (Apache+MySQL+PHP 7.4+)

**Backend Setup:**
```
1. C:\xampp\htdocs\redes\api  (copiar carpeta api)
2. phpMyAdmin: Importar setup_database.sql
3. Apache + MySQL: correr desde XAMPP Control Panel
4. Test: http://localhost/redes/api/turnos/stats.php
```

**Android Setup:**
```
1. git clone && git checkout dev
2. Gradle sync
3. ApiClient.kt: Configurar BASE_URL según entorno
4. Run en emulador (10.0.2.2) o dispositivo real ([IP])
```

**APK Build:** `./gradlew assembleDebug` | `./gradlew assembleRelease`

**Dependencias principales:** AndroidX, Material, ConstraintLayout, RecyclerView, **Coroutines 1.7.3**

----------

## 11. Internacionalización

**Idiomas:** Español (values/strings.xml), Inglés (values-en/strings.xml)
**Cambio dinámico:** `setLocale(lang)` → `Locale.setDefault()` + `config.setLocale()` + `recreate()`

----------

## 12. Mejoras Futuras

**Backend:** JWT auth, tokens sesión, rate limiting, logs auditoría
**Negocio:** Auto-asignación turnos, push notifications, prioridades, analíticas
**Arquitectura:** MVVM, ViewModel, Repository Pattern, Navigation Component
**UI/UX:** Animaciones, dark mode, accesibilidad mejorada
**Testing:** Tests unitarios, integración, UI (Espresso)

----------

## 13. Mantenimiento

**Gradle:** `clean`, `test`, `dependencies`, `dependencyUpdates`
**Debug:** Logcat, Android Profiler, Layout Inspector
**Versionado:** SemVer (MAJOR.MINOR.PATCH)

----------

## 14. Contribuciones

Repositorio: `redes` | Owner: `Giancarlo174` | Rama principal: `main`
**Pasos:** Fork → Branch → Changes → Commit → PR

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
| 2.1     | 02/12/2025  | dev  | **Validaciones y Multi-sucursal:** Validación de turno pendiente, desplegable de sucursales en AdminActivity, auto-refresh (3s admin/5s cliente), filtrado por sucursal en todos endpoints, nuevo endpoint /sucursales/list.php, UI mejorada |
| 2.0     | 30/11/2025  | dev  | **Backend completo:** API REST PHP, autenticación, LoginActivity, CustomerWelcomeActivity, ApiClient, Coroutines, integración MySQL, 7 endpoints REST, migración AdminActivity a Kotlin |
| 1.2     | 17/11/2025  | main | Implementación de RecyclerView, TurnAdapter, modelo Turn, migración de AdminActivity a Java, tabla sucursales |
| 1.1     | 11/11/2025  | main | Definición del modelo completo de base de datos (4 tablas)  |
| 1.0     | 11/11/2025  | main | Versión inicial del proyecto                                 |

**Nota:** Las versiones 2.0+ están en la rama `dev` y contienen integración completa con backend PHP + MySQL.

