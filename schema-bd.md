### 1. **usuarios**

Guarda la información de cada persona que usa el sistema (cliente o administrador).

| Campo            | Tipo                     | Descripción                     |
| ---------------- | ------------------------ | ------------------------------- |
| `id`             | INT, PK, AI              | Identificador único del usuario |
| `nombre`         | VARCHAR(100)             | Nombre del usuario              |
| `email`          | VARCHAR(255), UNIQUE     | Correo electrónico              |
| `password`       | VARCHAR(255)             | Contraseña encriptada           |
| `rol`            | ENUM('cliente', 'admin') | Define permisos                 |
| `fecha_registro` | DATETIME (CURRENT)       | Cuándo se registró el usuario   |

---

### 2. **turnos**

Registra los turnos agendados por los clientes.

| Campo            | Tipo                                                       | Descripción                           |
| ---------------- | ---------------------------------------------------------- | ------------------------------------- |
| `id`             | INT, PK, AI                                                | Identificador único del turno         |
| `id_usuario`     | INT, FK → usuarios.id_usuario                              | Cliente que reservó el turno          |
| `numero_turno`   | TEXT                                                       | Ejemplo: “A12”                        |
| `estado`         | ENUM('pendiente', 'cancelado', 'completado')               | Estado actual                         |
| `fecha_hora`     | DATETIME                                                   | Fecha y Hora del turno                |
| `fecha_creacion` | DATETIME (CURRENT)                                         | Cuándo se creó la reserva             |

---

### 3. **notificaciones**

Guarda las notificaciones para los clientes.

| Campo             | Tipo                                  | Descripción                     |
| ----------------- | ------------------------------------- | ------------------------------- |
| `id`              | INT, PK, AI                           | Identificador único             |
| `id_usuario`      | INT, FK → usuarios.id_usuario         | Usuario destinatario            |
| `titulo`          | VARCHAR(100)                          | Título breve de la notificación |
| `mensaje`         | VARCHAR(255)                          | Contenido del mensaje           |
| `leida`           | BOOLEAN, DEFAULT 0                    | Si ya fue leída                 |
| `fecha_envio`     | DATETIME                              | Cuándo se envió                 |