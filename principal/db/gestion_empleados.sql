-- =====================================================
-- Base de datos: gestion_empleados
-- Sistema de Asistencia y Permisos
-- Motor: MySQL 9.x / InnoDB / utf8mb4
-- Script re-ejecutable: elimina y recrea las tablas.
--
-- Ejecutar con:
--   mysql -u root --default-character-set=utf8mb4 < gestion_empleados.sql
-- =====================================================

SET NAMES utf8mb4;

DROP DATABASE IF EXISTS gestion_empleados;
CREATE DATABASE gestion_empleados
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE gestion_empleados;

-- =====================================================
-- TABLA: Rol
-- =====================================================
CREATE TABLE Rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: Usuario
-- =====================================================
CREATE TABLE Usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(50) NOT NULL,
    apellidos VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_usuario_rol FOREIGN KEY (id_rol) REFERENCES Rol(id_rol)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    INDEX idx_usuario_email (email)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: Turno
-- =====================================================
CREATE TABLE Turno (
    id_turno INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    hora_entrada TIME NOT NULL,
    hora_salida TIME NOT NULL,
    hora_ini_refrigerio TIME,
    hora_fin_refrigerio TIME
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: Horario_Empleado (asignación por día de la semana)
-- =====================================================
CREATE TABLE Horario_Empleado (
    id_horario INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_turno INT NOT NULL,
    dia_semana ENUM('LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO', 'DOMINGO') NOT NULL,

    CONSTRAINT fk_horario_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_horario_turno FOREIGN KEY (id_turno) REFERENCES Turno(id_turno)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    UNIQUE KEY uk_usuario_dia (id_usuario, dia_semana)
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

    CONSTRAINT fk_asistencia_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    UNIQUE KEY uk_usuario_fecha (id_usuario, fecha)
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: Tipo_Permiso
-- =====================================================
CREATE TABLE Tipo_Permiso (
    id_tipo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255),
    dias_maximos INT NOT NULL DEFAULT 0
) ENGINE=InnoDB;

-- =====================================================
-- TABLA: Permiso
-- =====================================================
CREATE TABLE Permiso (
    id_permiso INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_tipo INT NOT NULL,
    id_admin_responde INT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    motivo TEXT,
    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') NOT NULL DEFAULT 'PENDIENTE',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_respuesta DATETIME,

    CONSTRAINT fk_permiso_usuario FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_permiso_tipo FOREIGN KEY (id_tipo) REFERENCES Tipo_Permiso(id_tipo)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,

    CONSTRAINT fk_permiso_admin FOREIGN KEY (id_admin_responde) REFERENCES Usuario(id_usuario)
        ON DELETE SET NULL
        ON UPDATE CASCADE,

    INDEX idx_permiso_estado (estado),
    INDEX idx_permiso_usuario (id_usuario)
) ENGINE=InnoDB;

-- =====================================================
-- DATOS SEMILLA
-- =====================================================

-- Roles
INSERT INTO Rol (nombre) VALUES
('ADMIN'),
('EMPLEADO');

-- Tipos de permiso
INSERT INTO Tipo_Permiso (nombre, descripcion, dias_maximos) VALUES
('Vacaciones', 'Solicitud de vacaciones anuales', 15),
('Permiso', 'Permiso por asuntos personales', 3),
('Incapacidad', 'Incapacidad médica', 30),
('Duelo', 'Permiso por duelo familiar', 5);

-- Turno de ejemplo
INSERT INTO Turno (nombre, hora_entrada, hora_salida, hora_ini_refrigerio, hora_fin_refrigerio) VALUES
('Mañana', '08:00:00', '17:00:00', '13:00:00', '14:00:00');

-- Usuarios
-- Contraseñas BCrypt reales:
--   Admin123!    -> $2a$10$MUexy4SlCAf7uWz1MvCCQOGHFJ3.Z3VKyu2s6bU5gs2QR8F1YSzWy
--   Empleado123! -> $2a$10$bY24.V3kT1X8h3VqpDyQ6eOUaIrg27QNbL2WHzcZJCqoVIqm0FkLW
INSERT INTO Usuario (nombres, apellidos, email, password_hash, id_rol) VALUES
('Administrador', 'Sistema', 'admin@empresa.com', '$2a$10$MUexy4SlCAf7uWz1MvCCQOGHFJ3.Z3VKyu2s6bU5gs2QR8F1YSzWy', 1),
('María', 'López Sánchez', 'maria.lopez@empresa.com', '$2a$10$MUexy4SlCAf7uWz1MvCCQOGHFJ3.Z3VKyu2s6bU5gs2QR8F1YSzWy', 1),
('Juan', 'Pérez García', 'juan.perez@empresa.com', '$2a$10$bY24.V3kT1X8h3VqpDyQ6eOUaIrg27QNbL2WHzcZJCqoVIqm0FkLW', 2),
('Carlos', 'Martínez Ruiz', 'carlos.martinez@empresa.com', '$2a$10$bY24.V3kT1X8h3VqpDyQ6eOUaIrg27QNbL2WHzcZJCqoVIqm0FkLW', 2);

-- Horarios de ejemplo: turno "Mañana" de lunes a viernes para los empleados
INSERT INTO Horario_Empleado (id_usuario, id_turno, dia_semana) VALUES
(3, 1, 'LUNES'), (3, 1, 'MARTES'), (3, 1, 'MIERCOLES'), (3, 1, 'JUEVES'), (3, 1, 'VIERNES'),
(4, 1, 'LUNES'), (4, 1, 'MARTES'), (4, 1, 'MIERCOLES'), (4, 1, 'JUEVES'), (4, 1, 'VIERNES');
