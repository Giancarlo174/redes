-- ============================================
--  BASE DE DATOS: turno_facil
-- ============================================

CREATE DATABASE IF NOT EXISTS turno_facil;

-- ============================================
--  TABLA: usuarios
-- ============================================

CREATE TABLE usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_sucursal INT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  rol ENUM('cliente', 'admin') DEFAULT 'cliente',
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
--  TABLA: turnos
-- ============================================

CREATE TABLE turnos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  id_sucursal INT NOT NULL,
  numero_turno TEXT NOT NULL,
  estado ENUM('pendiente', 'cancelado', 'completado') DEFAULT 'pendiente',
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  actualizado_en DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- ============================================
--  TABLA: notificaciones
-- ============================================

CREATE TABLE notificaciones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  titulo VARCHAR(100) NOT NULL,
  mensaje VARCHAR(255) NOT NULL,
  leida BOOLEAN DEFAULT 0,
  fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);


-- ============================================
--  TABLA: sucursales
-- ============================================

CREATE TABLE sucursales (
  id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  ubicacion VARCHAR(255) NOT NULL,
  telefono VARCHAR(20)
);


--------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------
--------------------------------------------------------------------------------------

-- ============================================
--  BASE DE DATOS: turno_facil
-- ============================================

CREATE DATABASE IF NOT EXISTS turno_facil;
USE turno_facil;

-- ============================================
--  TABLA: sucursales
-- ============================================
-- Primero: porque usuarios y turnos dependen de ella

CREATE TABLE IF NOT EXISTS sucursales (
  id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  ubicacion VARCHAR(255) NOT NULL,
  telefono VARCHAR(20)
);

-- ============================================
--  TABLA: usuarios
-- ============================================

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_sucursal INT NOT NULL,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  rol ENUM('cliente', 'admin') DEFAULT 'cliente',
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- ============================================
--  TABLA: turnos
-- ============================================

CREATE TABLE IF NOT EXISTS turnos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  id_sucursal INT NOT NULL,
  numero_turno VARCHAR(50) NOT NULL,
  estado ENUM('pendiente', 'cancelado', 'completado') DEFAULT 'pendiente',
  fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
  actualizado_en DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

  FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,

  FOREIGN KEY (id_sucursal) REFERENCES sucursales(id_sucursal)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- ============================================
--  TABLA: notificaciones
-- ============================================

CREATE TABLE IF NOT EXISTS notificaciones (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  titulo VARCHAR(100) NOT NULL,
  mensaje VARCHAR(255) NOT NULL,
  leida BOOLEAN DEFAULT 0,
  fecha_envio DATETIME DEFAULT CURRENT_TIMESTAMP,

  FOREIGN KEY (id_usuario) REFERENCES usuarios(id)
    ON UPDATE CASCADE
    ON DELETE CASCADE
);

-- ============================================
--  INSERTAR DATOS DE PRUEBA - SUCURSALES
-- ============================================

INSERT INTO sucursales (nombre, ubicacion, telefono) VALUES
('Costa Verde', 'La Chorrera, Panamá Oeste', '507-800-0001'),
('Panamá', 'Ave. Balboa, Ciudad de Panamá', '507-800-0002'),
('Chiriquí', 'David, Provincia de Chiriquí', '507-800-0003'),
('Todos', 'Todas las sucursales', '507-800-0000');


-- ============================================
--  INSERTAR DATOS DE PRUEBA - USUARIOS
-- ============================================
-- Nota: ahora usuarios requieren id_sucursal

INSERT INTO usuarios (id_sucursal, nombre, email, password, rol, fecha_registro) VALUES
(1, 'Administrador', 'admin@turnofacil.com', 'admin123', 'admin', NOW()),
(1, 'Juan Pérez', 'juan.perez@email.com', 'pass123', 'cliente', NOW()),
(2, 'María García', 'maria.garcia@email.com', 'pass123', 'cliente', NOW()),
(3, 'Carlos López', 'carlos.lopez@email.com', 'pass123', 'cliente', NOW()),
(2, 'Ana Martínez', 'ana.martinez@email.com', 'pass123', 'cliente', NOW());

-- ============================================
--  INSERTAR DATOS DE PRUEBA - TURNOS
-- ============================================

INSERT INTO turnos (id_usuario, id_sucursal, numero_turno, estado, fecha_creacion, actualizado_en) VALUES
(2, 1, 'A12', 'pendiente', '2025-11-30 14:20:00', '2025-11-30 14:20:00'),
(3, 2, 'B05', 'pendiente', '2025-11-30 14:22:00', '2025-11-30 14:22:00'),
(4, 3, 'C18', 'completado', '2025-11-30 14:15:00', '2025-11-30 14:25:00'),
(5, 2, 'A25', 'pendiente', '2025-11-30 14:24:00', '2025-11-30 14:24:00'),
(2, 1, 'D09', 'cancelado', '2025-11-30 14:10:00', '2025-11-30 14:18:00');

-- ============================================
--  VERIFICAR DATOS INSERTADOS
-- ============================================

SELECT 'Usuarios creados:' AS mensaje;
SELECT * FROM usuarios;

SELECT 'Turnos creados:' AS mensaje;
SELECT * FROM turnos;

SELECT 'Sucursales creadas:' AS mensaje;
SELECT * FROM sucursales;