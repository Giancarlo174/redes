
# Documentación Técnica – Turno Fácil

## 1. Información General

Turno Fácil es una aplicación móvil Android diseñada para gestionar turnos en establecimientos como bancos, consultorios médicos y oficinas públicas. Permite a los clientes solicitar y consultar turnos, y a los administradores gestionar el flujo de atención.

**Datos del proyecto:**

-   Nombre: Turno Fácil
    
-   Package: com.example.turnofacil
    
-   Versión: 1.0
    
-   Lenguajes usados: Kotlin y Java
    
-   Plataforma: Android
    
-   SDK mínimo: 24 (Android 7.0)
    
-   SDK objetivo: 36 (Android 14)
    

**Propósito:**  
Facilitar la administración de turnos, mejorar la organización y ofrecer una experiencia de usuario simple y eficiente.

---------
## 2. Arquitectura del Sistema

La aplicación utiliza una arquitectura basada en Activities, adecuada para proyectos Android de bajo y mediano alcance.

**Diagrama general:**

```
MainActivity
 ├── CustomerActivity
 └── AdminActivity
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
        
4.  **Lógica de negocio**
    
    -   Manejo de turnos en memoria
        
    -   Estados del turno
        
    -   Navegación entre pantallas
        
    -   Cambio de idioma
        
5.  **Capa Base**
    
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
│   │   ├── AdminActivity.java
│   │   ├── CustomerActivity.kt
│   │   ├── Turn.kt
│   │   └── TurnAdapter.kt
│   ├── res/
│   │   ├── layout/
│   │   ├── values/
│   │   ├── values-en/
│   │   ├── drawable/
│   │   └── mipmap-*/
│   └── AndroidManifest.xml
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

----------

## 5. Componentes Principales

### 5.1 MainActivity

Pantalla principal de la aplicación. Desde aquí el usuario puede seleccionar si es cliente o administrador y cambiar el idioma del sistema.

**Funciones principales:**

-   Navegación a CustomerActivity
    
-   Navegación a AdminActivity
    
-   Cambio dinámico de idioma mediante setLocale()
    

----------

### 5.2 BaseActivity

Clase abstracta utilizada por algunas Activities para unificar el comportamiento del botón "Atrás" en la ActionBar.

----------

### 5.3 AdminActivity

Pantalla destinada a los administradores del sistema.

**Funciones:**

-   Gestionar turnos activos
    
-   Ver estadísticas (en espera, atendidos, cancelados)
    
-   Llamar al siguiente turno
    
-   Cancelar turnos
    
-   Interfaz simple basada en XML y elementos Material Design
    

AdminActivity se encuentra implementada en Java y ya no hereda de BaseActivity.

----------

### 5.4 CustomerActivity

Pantalla destinada a los clientes que desean solicitar o consultar su turno.

**Funciones:**

-   Mostrar lista de turnos mediante RecyclerView
    
-   Generar un nuevo turno
    
-   Mostrar turno en atención
    
-   Destacar visualmente los turnos en espera y el turno actual
    
-   Navegar de regreso a la pantalla principal
    

----------

### 5.5 Turn (Modelo)

Clase de datos que representa un turno dentro de la aplicación.

```
data class Turn(
    val turnNumber: String,
    val status: String,
    val isAttending: Boolean = false
)
```

----------

### 5.6 TurnAdapter

Adaptador utilizado para mostrar la lista de turnos en CustomerActivity.

**Responsabilidades:**

-   Inflar elementos de lista
    
-   Asignar datos de Turn a cada vista
    
-   Aplicar estilos visuales según el estado del turno
    

----------

## 6. Flujo de Navegación

```
MainActivity
 ├── Cliente → CustomerActivity → Lista de turnos (TurnAdapter)
 └── Administrador → AdminActivity
```

**Descripción:**

-   La aplicación inicia en MainActivity
    
-   El cliente accede a CustomerActivity
    
-   El administrador accede a AdminActivity
    
-   Ambas pantallas permiten regresar a la principal mediante el botón atrás
    

----------

## 7. Modelo de Base de Datos

Aunque la aplicación aún no implementa persistencia real, el diseño propuesto contempla una base de datos relacional compuesta por tres tablas principales:

### 7.1 Tabla usuarios

| Campo           | Tipo                    | Descripción              |
|-----------------|-------------------------|--------------------------|
| id              | INT PK AI               | Identificador único      |
| nombre          | VARCHAR(100)            | Nombre del usuario       |
| email           | VARCHAR(255) UNIQUE     | Correo único             |
| password        | VARCHAR(255)            | Hash de contraseña       |
| rol             | ENUM(cliente, admin)    | Rol del usuario          |
| fecha_registro  | DATETIME                | Fecha de registro        |

----------

### 7.2 Tabla turnos

| Campo          | Tipo                                      | Descripción               |
|----------------|-------------------------------------------|---------------------------|
| id             | INT PK AI                                  | ID del turno              |
| id_usuario     | INT FK                                     | Usuario dueño del turno   |
| numero_turno   | VARCHAR(50)                                | Código del turno          |
| estado         | ENUM(pendiente, cancelado, completado)     | Estado del turno          |
| fecha_hora     | DATETIME                                   | Fecha programada          |
| fecha_creacion | DATETIME                                   | Fecha de creación         |


----------

### 7.3 Tabla notificaciones
| Campo        | Tipo              | Descripción                  |
|--------------|-------------------|------------------------------|
| id           | INT PK AI         | ID de la notificación        |
| id_usuario   | INT FK            | Usuario destinatario         |
| titulo       | VARCHAR(100)      | Título del mensaje           |
| mensaje      | VARCHAR(255)      | Contenido del mensaje        |
| leida        | BOOLEAN           | Indicador de lectura         |
| fecha_envio  | DATETIME          | Fecha de envío               |


----------

### Relaciones del modelo

```
usuarios (1) <--> (N) turnos
usuarios (1) <--> (N) notificaciones
```

----------

## 8. Características Implementadas

-   Navegación mediante Intents
    
-   Botón atrás funcional
    
-   Diseño Material Design
    
-   Soporte multi-idioma (español e inglés)
    
-   RecyclerView con adaptadores personalizados
    
-   Vista mejorada para turno en atención
    
-   Estructura del proyecto clara y modular
    
-   Uso de data classes y ViewHolder Pattern
    

----------

## 9. Configuración del Proyecto

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
    

----------

## 10. Guía de Compilación

### Requisitos previos

-   Instalar Android Studio
    
-   Instalar JDK 11
    
-   Tener las SDK necesarias (API 24–36)
    

### Pasos

1.  Clonar el repositorio
    
    ```
    git clone <url>
    cd redes
    
    ```
    
2.  Abrir en Android Studio
    
3.  Sincronizar Gradle
    
4.  Ejecutar la app en dispositivo o emulador
    

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

## 12. Mejoras Futuras

### Backend

-   Implementación de API REST
    
-   Autenticación con JWT
    
-   Persistencia real en MySQL o PostgreSQL
    

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

| Versión | Fecha       | Cambios                                                      |
|---------|-------------|--------------------------------------------------------------|
| 1.2     | 17/11/2025  | Implementación de RecyclerView, TurnAdapter y migración de AdminActivity a Java |
| 1.1     | 11/11/2025  | Definición del modelo completo de base de datos             |
| 1.0     | 11/11/2025  | Versión inicial del proyecto                                 |

