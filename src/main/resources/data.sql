CREATE TABLE IF NOT EXISTS auditoria (
                                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                         usuario_id BIGINT,
                                         accion VARCHAR(255),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );