package com.backend.avance1.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageServiceInterface {

    String guardarArchivo(String dni, MultipartFile file, String nombreArchivo);

    boolean eliminarArchivo(String ruta);

    boolean eliminarCarpetaUsuario(String dni);
}