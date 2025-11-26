-- ================================================
-- Datos de la tabla user (password : admin123)
-- ================================================
INSERT INTO user (id, activo, fecha_registro, apellidos, celular, direccion, dni_ruc, email, nombres, password) VALUES
                                                                                                                    (1, true, '2025-11-26 05:15:14', 'Lopez', '930944873', 'Av. Principal 123', '70944565', 'admin@rutasprime.com', 'Jean', '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm'),
                                                                                                                    (2, true, '2025-11-26 05:15:14', 'Perez', '934567890', 'Calle Ficticia 456', '12345678', 'josereyy029@gmail.com', 'Ana', '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm'),
                                                                                                                    (3, true, '2025-11-26 05:15:14', 'Ramirez', '987654321', 'Av. Las Flores 890', '87654321', 'admin1@rutasprime.com', 'Carlos', '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm'),
                                                                                                                    (4, true, '2025-11-26 05:15:14', 'Torres', '912345678', 'Jr. Los Alamos 222', '55667788', 'admin2@rutasprime.com', 'Lucia', '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm'),
                                                                                                                    (5, true, '2025-11-26 05:15:14', 'Fernandez', '956789432', 'Av. Las Gardenias 999', '44556677', 'cliente1@rutasprime.com', 'Mario', '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm'),
                                                                                                                    (6, true, '2025-11-26 05:15:14', 'Gomez', '945123678', 'Calle Los Pinos 777', '77889900', 'cliente2@rutasprime.com', 'Sofia', '$2a$10$47Jzqtc52KOceWAU3PU/uO/NcfVUQl7r3n2/XNUtxjXKF8UaNwqEm');

-- ================================================
-- Datos de la tabla user_roles
-- ================================================
INSERT INTO user_roles (user_id, role) VALUES
                                           (1, 'ROLE_SUPERADMIN'),
                                           (2, 'ROLE_CLIENTE'),
                                           (3, 'ROLE_ADMIN'),
                                           (4, 'ROLE_ADMIN'),
                                           (5, 'ROLE_CLIENTE'),
                                           (6, 'ROLE_CLIENTE'),
                                           (2, 'ROLE_CONDUCTOR');

-- ================================================
-- Datos de la tabla conductor_info
-- ================================================
INSERT INTO conductor_info (
    fecha_solicitud, id, user_id, anio_fabricacion, antecedentes_penales, codigo_solicitud, color,
    fecha_nacimiento, foto_licencia, foto_persona_licencia, marca, numero_licencia_conducir,
    observacion_admin, placa, revision_tecnica, soat, tarjeta_circulacion, tarjeta_propiedad, estado
) VALUES
    ('2025-11-26 11:29:01.149552', 1, 2, '2017', 'http://localhost:8080/api/archivos/12345678/antecedentes_penales.jpg', 'COND-20251126-1CB94', 'Rojo',
     '2004-02-07', 'http://localhost:8080/api/archivos/12345678/foto_licencia.jpg', 'http://localhost:8080/api/archivos/12345678/foto_persona_licencia.jpg', 'Nissan', 'B08409242',
     'TODO OK', 'ABC900', 'http://localhost:8080/api/archivos/12345678/revision_tecnica.jpg', 'http://localhost:8080/api/archivos/12345678/soat.jpg', 'http://localhost:8080/api/archivos/12345678/tarjeta_circulacion.jpg', 'http://localhost:8080/api/archivos/12345678/tarjeta_propiedad.jpg', 'APROBADO');

-- ================================================
-- Datos de la tabla conductor_info_historial
-- ================================================
INSERT INTO conductor_info_historial (
    conductor_info_id, fecha_cambio, id, user_id, codigo_solicitud, observacion, estado
) VALUES
      (1, '2025-11-26 11:29:01.252337', 1, 2, 'COND-20251126-1CB94', 'Solicitud inicial creada.', 'PENDIENTE'),
      (1, '2025-11-26 11:30:40.474900', 2, 2, 'COND-20251126-1CB94', 'Estado actualizado a APROBADO - TODO OK', 'APROBADO');

-- ================================================
-- Datos de la tabla contact_messages
-- ================================================
INSERT INTO contact_messages (
    created_at, id, message, email, message_code, name
) VALUES
    ('2025-11-26 11:33:27.729685', 1, 'Necesito ayuda con mi empresa', 'josereyy029@gmail.com', 'RP-2025-0001', 'Andre Palomino');

-- ================================================
-- Datos de la tabla contact_replies
-- ================================================
INSERT INTO contact_replies (
    contact_message_id, id, replied_at, reply_code, reply_message
) VALUES
    (1, 1, '2025-11-26 11:35:03.281252', 'RP-2025-0002-R1', 'PROBANDO');