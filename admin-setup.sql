-- Script para configurar un usuario como ADMIN en FitBoost
-- Ejecutar en MySQL después de registrar un usuario

-- Opción 1: Actualizar un usuario existente a ADMIN (reemplaza 'tu_usuario' con tu username)
UPDATE users SET role = 'ADMIN' WHERE username = 'tu_usuario';

-- Opción 2: Ver todos los usuarios actuales
SELECT id, username, email, role FROM users;

-- Opción 3: Si necesitas crear un usuario ADMIN directamente con contraseña encriptada
-- (La contraseña debe ser un hash BCrypt válido)
-- INSERT INTO users (username, password, email, role) 
-- VALUES ('admin', '$2a$10$slYQmyNdGzin7olVN3p5Be7DQftYnyKvNB2.UhBp4RP1.GYy3dSJ2', 'admin@fitboost.com', 'ADMIN');
-- Nota: El hash anterior es BCrypt de "password123"
