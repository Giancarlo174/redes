# API REST - Turno Fácil

## Configuración

1. Asegúrate de tener XAMPP instalado y ejecutándose
2. Copia la carpeta `api` en `c:\xampp\htdocs\redes\`
3. Importa el script SQL en phpMyAdmin
4. La API estará disponible en: `http://localhost/redes/api/`

## Endpoints Disponibles

### Autenticación

#### Login (Admin o Cliente)
**POST** `/auth/login.php`

**Descripción:** Permite el login de usuarios con rol `admin` o `cliente`. Redirige automáticamente según el rol.

**Request:**
```json
{
  "email": "admin@turnofacil.com",
  "password": "admin123"
}
```

**Respuesta exitosa:**
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

**Respuesta de error:**
```json
{
  "success": false,
  "message": "Email o contraseña incorrectos"
}
```

**Roles soportados:**
- `admin`: Accede al panel de administración (AdminActivity)
- `cliente`: Accede a la vista de cliente (CustomerActivity)

### Turnos

#### Crear Turno
**POST** `/turnos/create.php`

```json
{
  "nombre": "Juan Pérez",
  "cedula": "12345678"
}
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Turno generado exitosamente",
  "data": {
    "turno_id": 5,
    "numero_turno": "A05",
    "usuario_id": 3,
    "nombre": "Juan Pérez",
    "email": "12345678@turno.facil.com"
  }
}
```

#### Listar Turnos
**GET** `/turnos/list.php?estado=pendiente&fecha=2025-11-30`

Parámetros opcionales:
- `estado`: pendiente, completado, cancelado
- `fecha`: formato YYYY-MM-DD (por defecto hoy)

**Respuesta:**
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "numero_turno": "A12",
      "estado": "pendiente",
      "fecha_creacion": "2025-11-30 14:20:00",
      "actualizado_en": "2025-11-30 14:20:00",
      "nombre": "Juan Pérez",
      "email": "juan.perez@email.com"
    }
  ],
  "count": 1
}
```

#### Listar Turnos de Usuario
**GET** `/turnos/list_user.php?id_usuario=1&fecha=2025-11-30`

**Descripción:** Obtiene los turnos de un usuario específico con información adicional sobre su posición en la cola.

Parámetros requeridos:
- `id_usuario`: ID del usuario

Parámetros opcionales:
- `fecha`: formato YYYY-MM-DD (por defecto hoy)

**Respuesta:**
```json
{
  "success": true,
  "data": {
    "turnos": [
      {
        "id": 5,
        "numero_turno": "A05",
        "estado": "pendiente",
        "fecha_creacion": "2025-11-30 14:30:00",
        "actualizado_en": "2025-11-30 14:30:00"
      }
    ],
    "turno_en_atencion": "A12",
    "posicion_en_cola": 2
  },
  "count": 1
}
```

#### Actualizar Estado de Turno
**PUT** `/turnos/update.php`

```json
{
  "id": 1,
  "estado": "completado"
}
```

Estados válidos: `pendiente`, `completado`, `cancelado`

#### Estadísticas
**GET** `/turnos/stats.php?fecha=2025-11-30`

**Respuesta:**
```json
{
  "success": true,
  "data": {
    "en_espera": 4,
    "atendidos": 0,
    "cancelados": 0,
    "total": 4
  }
}
```

## Configuración Android

En `ApiClient.kt`, cambia la URL base según tu entorno:

- **Emulador Android**: `http://10.0.2.2/redes/api`
- **Dispositivo Real**: `http://TU_IP_LOCAL/redes/api` (ej: `http://192.168.1.100/redes/api`)

Para obtener tu IP local:
```bash
ipconfig
# Busca "Dirección IPv4" en tu adaptador de red WiFi o Ethernet
```

## Credenciales de Prueba

**Admin:**
- Email: `admin@turnofacil.com`
- Password: `admin123`
- Rol: `admin`

**Clientes de prueba:**
- Email: `juan.perez@email.com` / Password: `pass123`
- Email: `maria.garcia@email.com` / Password: `pass123`
- Email: `carlos.lopez@email.com` / Password: `pass123`
- Rol: `cliente`

**Nuevos clientes:**
Los clientes se crean automáticamente al tomar un turno con:
- Nombre: cualquier nombre
- Cédula: cualquier número
- Email: se genera automáticamente como `[cedula]@turno.facil.com`
- Password: mismo valor que la cédula
- Rol: `cliente`

Ver más detalles en `CREDENCIALES_PRUEBA.md`
