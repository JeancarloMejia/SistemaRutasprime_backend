-- Crear usuario superadmin
INSERT INTO user (id, activo, fecha_registro, apellidos, celular, direccion, dni_ruc, email, nombres, password)
VALUES (
           1,
           true,
           NOW(),
           'Lopez',
           '930944873',
           'Av. Principal 123',
           '70944565',
           'admin@rutasprime.com',
           'Jean',
           '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm' -- contraseña: admin123
       );
INSERT INTO user_roles (user_id, role)
VALUES (1, 'ROLE_SUPERADMIN');

-- Crear usuario con rol ROLE_CLIENTE
INSERT INTO user (id, activo, fecha_registro, apellidos, celular, direccion, dni_ruc, email, nombres, password)
VALUES (
           2,
           true,
           NOW(),
           'Perez',
           '934567890',
           'Calle Ficticia 456',
           '12345678',
           'josereyy029@gmail.com',
           'Ana',
           '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm' -- contraseña: admin123
       );
INSERT INTO user_roles (user_id, role)
VALUES (2, 'ROLE_CLIENTE');

-- ================================================
-- NUEVOS USUARIOS ROLE_ADMIN
-- ================================================

INSERT INTO user (id, activo, fecha_registro, apellidos, celular, direccion, dni_ruc, email, nombres, password)
VALUES
    (3, true, NOW(), 'Ramirez', '987654321', 'Av. Las Flores 890', '87654321', 'admin1@rutasprime.com', 'Carlos',
     '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm'), -- contraseña: admin123
    (4, true, NOW(), 'Torres', '912345678', 'Jr. Los Alamos 222', '55667788', 'admin2@rutasprime.com', 'Lucia',
     '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm'); -- contraseña: admin123

INSERT INTO user_roles (user_id, role)
VALUES
    (3, 'ROLE_ADMIN'),
    (4, 'ROLE_ADMIN');

-- ================================================
-- NUEVOS USUARIOS ROLE_CLIENTE
-- ================================================

INSERT INTO user (id, activo, fecha_registro, apellidos, celular, direccion, dni_ruc, email, nombres, password)
VALUES
    (5, true, NOW(), 'Fernandez', '956789432', 'Av. Las Gardenias 999', '44556677', 'cliente1@rutasprime.com', 'Mario',
     '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm'), -- contraseña: admin123
    (6, true, NOW(), 'Gomez', '945123678', 'Calle Los Pinos 777', '77889900', 'cliente2@rutasprime.com', 'Sofia',
     '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm'); -- contraseña: admin123

INSERT INTO user_roles (user_id, role)
VALUES
    (5, 'ROLE_CLIENTE'),
    (6, 'ROLE_CLIENTE');