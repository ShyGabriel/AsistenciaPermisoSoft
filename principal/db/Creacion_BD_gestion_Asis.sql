-- =====================================================
-- Sistema de Gestión de Empleados, Asistencia y Solicitudes
-- =====================================================

-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS gestion_empleados
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_empleados;

-- =====================================================
-- TABLA: Usuario
-- =====================================================
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,  -- ✅ UNIQUE agregado
    password_hash VARCHAR(255) NOT NULL,
    rol ENUM('EMPLEADO', 'ADMIN') NOT NULL DEFAULT 'EMPLEADO',
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Índice para búsquedas por email
    INDEX idx_email (email)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: Tipo_Solicitud
-- =====================================================
CREATE TABLE Tipo_Solicitud (
    id_tipo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    dias_maximos INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: Asistencia
-- =====================================================
CREATE TABLE Asistencia (
    id_asistencia INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    fecha DATE NOT NULL,
    hora_entrada TIME,
    hora_ini_refrigerio TIME,
    hora_fin_refrigerio TIME,
    hora_salida TIME,
    estado ENUM('COMPLETA', 'INCOMPLETA', 'AUSENTE') NOT NULL DEFAULT 'INCOMPLETA',
    
    -- Clave foránea
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    
    -- ✅ UNIQUE compuesto para evitar duplicados en el mismo día
    UNIQUE KEY uk_usuario_fecha (id_usuario, fecha),
    
    -- ✅ Índice compuesto para búsquedas rápidas
    INDEX idx_usuario_fecha (id_usuario, fecha)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: Solicitud
-- =====================================================
CREATE TABLE Solicitud (
    id_solicitud INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_tipo INT NOT NULL,
    id_admin_responde INT,  -- ✅ Eliminado el duplicado, queda solo uno
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    motivo TEXT,
    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',  -- ✅ ENUM completado
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta DATETIME,
    
    -- Claves foráneas
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    
    FOREIGN KEY (id_tipo) REFERENCES Tipo_Solicitud(id_tipo)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    
    FOREIGN KEY (id_admin_responde) REFERENCES Usuario(id_usuario)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    
    -- ✅ Índice para filtrar solicitudes pendientes rápidamente
    INDEX idx_estado (estado),
    
    -- ✅ Índice para búsquedas por usuario
    INDEX idx_usuario (id_usuario)
) ENGINE=InnoDB;

-- =====================================================
-- DATOS DE PRUEBA (Opcional)
-- =====================================================

-- Insertar tipos de solicitud
INSERT INTO Tipo_Solicitud (nombre, descripcion, dias_maximos) VALUES
('Vacaciones', 'Solicitud de vacaciones anuales', 15),
('Permiso', 'Permiso por asuntos personales', 3),
('Incapacidad', 'Incapacidad médica', 30),
('Duelo', 'Permiso por duelo familiar', 5);

-- Insertar usuarios de prueba
INSERT INTO Usuario (nombres, apellidos, email, password_hash, rol) VALUES
('Juan', 'Pérez García', 'juan.perez@empresa.com', '$2y$10$hash_de_ejemplo', 'EMPLEADO'),
('María', 'López Sánchez', 'maria.lopez@empresa.com', '$2y$10$hash_de_ejemplo', 'ADMIN'),
('Carlos', 'Martínez Ruiz', 'carlos.martinez@empresa.com', '$2y$10$hash_de_ejemplo', 'EMPLEADO');

-- =====================================================
-- CONSULTAS ÚTILES DE EJEMPLO
-- =====================================================

-- Ver todas las solicitudes pendientes
-- SELECT s.*, u.nombres, u.apellidos, ts.nombre AS tipo_solicitud
-- FROM Solicitud s
-- JOIN Usuario u ON s.id_usuario = u.id_usuario
-- JOIN Tipo_Solicitud ts ON s.id_tipo = ts.id_tipo
-- WHERE s.estado = 'PENDIENTE';

-- Ver asistencia de un usuario en un mes específico
-- SELECT * FROM Asistencia
-- WHERE id_usuario = 1
-- AND fecha BETWEEN '2024-01-01' AND '2024-01-31'
-- ORDER BY fecha;

-- Resumen de solicitudes por usuario
-- SELECT u.nombres, u.apellidos, 
--        COUNT(s.id_solicitud) AS total_solicitudes,
--        SUM(CASE WHEN s.estado = 'APROBADO' THEN 1 ELSE 0 END) AS aprobadas,
--        SUM(CASE WHEN s.estado = 'PENDIENTE' THEN 1 ELSE 0 END) AS pendientes
-- FROM Usuario u
-- LEFT JOIN Solicitud s ON u.id_usuario = s.id_usuario
-- GROUP BY u.id_usuario;