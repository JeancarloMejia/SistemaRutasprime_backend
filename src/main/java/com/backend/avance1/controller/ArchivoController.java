package com.backend.avance1.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.nio.file.*;
import org.springframework.beans.factory.annotation.Value;

@RestController
@RequestMapping("/api/archivos")
@CrossOrigin(origins = "*")
public class ArchivoController {

    @Value("${app.uploads.dir}")
    private String uploadsDir;

    @GetMapping("/{dni}/{nombreArchivo}")
    public ResponseEntity<Resource> obtenerArchivo(
            @PathVariable String dni,
            @PathVariable String nombreArchivo) {
        try {
            Path archivo = Paths.get(uploadsDir).resolve(dni).resolve(nombreArchivo);
            Resource recurso;

            if (Files.exists(archivo)) {
                recurso = new UrlResource(archivo.toUri());
            } else {
                recurso = new ClassPathResource("static/sin-archivo.png");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(archivo))
                    .body(recurso);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}