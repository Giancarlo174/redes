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
    ON DELETE CASCADE
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
--  TABLA: usuarios
-- ============================================

CREATE TABLE IF NOT EXISTS usuarios (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(255) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  rol ENUM('cliente', 'admin') DEFAULT 'cliente',
  fecha_registro DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
--  TABLA: turnos
-- ============================================

CREATE TABLE IF NOT EXISTS turnos (
  id INT AUTO_INCREMENT PRIMARY KEY,
  id_usuario INT NOT NULL,
  numero_turno VARCHAR(50) NOT NULL,
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
--  TABLA: sucursales
-- ============================================

CREATE TABLE IF NOT EXISTS sucursales (
  id_sucursal INT AUTO_INCREMENT PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  ubicacion VARCHAR(255) NOT NULL,
  telefono VARCHAR(20)
);

-- ============================================
--  INSERTAR DATOS DE PRUEBA - USUARIOS
-- ============================================

INSERT INTO usuarios (nombre, email, password, rol, fecha_registro) VALUES
('Administrador', 'admin@turnofacil.com', 'admin123', 'admin', NOW()),
('Juan Pérez', 'juan.perez@email.com', 'pass123', 'cliente', NOW()),
('María García', 'maria.garcia@email.com', 'pass123', 'cliente', NOW()),
('Carlos López', 'carlos.lopez@email.com', 'pass123', 'cliente', NOW()),
('Ana Martínez', 'ana.martinez@email.com', 'pass123', 'cliente', NOW());

-- ============================================
--  INSERTAR DATOS DE PRUEBA - TURNOS
-- ============================================

INSERT INTO turnos (id_usuario, numero_turno, estado, fecha_creacion, actualizado_en) VALUES
(2, 'A12', 'pendiente', '2025-11-30 14:20:00', '2025-11-30 14:20:00'),
(3, 'B05', 'pendiente', '2025-11-30 14:22:00', '2025-11-30 14:22:00'),
(4, 'C18', 'completado', '2025-11-30 14:15:00', '2025-11-30 14:25:00'),
(5, 'A25', 'pendiente', '2025-11-30 14:24:00', '2025-11-30 14:24:00'),
(2, 'D09', 'cancelado', '2025-11-30 14:10:00', '2025-11-30 14:18:00');


-- ============================================
--  INSERTAR DATOS DE PRUEBA - SUCURSALES
-- ============================================
INSERT INTO sucursales (nombre, ubicacion, telefono) VALUES
('Costa Verde', 'La Chorrera, Panamá Oeste', '507-800-0001'),
('Panamá', 'Ave. Balboa, Ciudad de Panamá', '507-800-0002'),
('Chiriquí', 'David, Provincia de Chiriquí', '507-800-0003');


-- ============================================
--  VERIFICAR DATOS INSERTADOS
-- ============================================

SELECT 'Usuarios creados:' as mensaje;
SELECT * FROM usuarios;

SELECT 'Turnos creados:' as mensaje;
SELECT * FROM turnos;