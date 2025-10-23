package com.backend.avance1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Logger;

@Service
public class FileStorageService implements FileStorageServiceInterface {

    private static final Logger LOGGER = Logger.getLogger(FileStorageService.class.getName());

    @Value("${app.uploads.dir}")
    private String uploadsDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    public String guardarArchivo(String dni, MultipartFile file, String nombreArchivo) {
        if (uploadsDir == null || uploadsDir.isBlank()) {
            throw new RuntimeException("La propiedad 'app.uploads.dir' no está configurada correctamente.");
        }

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo '" + nombreArchivo + "' no puede estar vacío.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("El archivo '" + nombreArchivo + "' excede los 5 MB permitidos.");
        }

        File carpetaUsuario = new File(uploadsDir, dni);
        if (!carpetaUsuario.exists() && !carpetaUsuario.mkdirs()) {
            throw new RuntimeException("No se pudo crear la carpeta para el usuario: " + dni);
        }

        String extension = obtenerExtension(file.getOriginalFilename());
        File destino = new File(carpetaUsuario, nombreArchivo + extension);

        try (var inputStream = file.getInputStream()) {
            java.nio.file.Files.copy(
                    inputStream,
                    destino.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
            );
            return destino.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo '" + nombreArchivo + "'", e);
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (Objects.isNull(nombreArchivo) || !nombreArchivo.contains(".")) {
            return "";
        }
        return nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
    }

    public boolean eliminarArchivo(String ruta) {
        if (ruta == null || ruta.isBlank()) return false;
        File archivo = new File(ruta);

        if (!archivo.exists()) {
            LOGGER.warning("Intento de eliminar un archivo inexistente: " + ruta);
            return false;
        }

        boolean eliminado = archivo.delete();
        if (!eliminado) {
            LOGGER.warning("No se pudo eliminar el archivo: " + ruta);
        }
        return eliminado;
    }

    public boolean eliminarCarpetaUsuario(String dni) {
        File carpeta = new File(uploadsDir, dni);
        if (!carpeta.exists() || !carpeta.isDirectory()) {
            LOGGER.warning("No se encontró la carpeta del usuario: " + dni);
            return false;
        }

        boolean exito = true;
        File[] archivos = carpeta.listFiles();

        if (archivos != null) {
            for (File archivo : archivos) {
                if (!archivo.delete()) {
                    LOGGER.warning("No se pudo eliminar el archivo: " + archivo.getName());
                    exito = false;
                }
            }
        }

        if (!carpeta.delete()) {
            LOGGER.warning("No se pudo eliminar la carpeta del usuario: " + carpeta.getAbsolutePath());
            exito = false;
        }

        return exito;
    }
}