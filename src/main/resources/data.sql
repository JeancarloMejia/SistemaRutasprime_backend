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