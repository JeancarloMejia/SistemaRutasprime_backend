package com.backend.avance1.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;


import static org.assertj.core.api.Assertions.*;

class FileStorageServiceTest {

    private final FileStorageService service = new FileStorageService();

    @Test
    void guardarArchivo_DeberiaLanzarError_CuandoRutaNoConfigurada() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "file.jpg", "image/jpeg", new byte[]{1,2,3});

        assertThatThrownBy(() ->
                service.guardarArchivo("123", mockFile, "foto")
        ).isInstanceOf(RuntimeException.class);
    }

    @Test
    void obtenerExtension_DeberiaRetornarExtensionCorrecta() throws Exception {
        FileStorageService s = new FileStorageService();
        String ext = s.getClass().getDeclaredMethod("obtenerExtension", String.class)
                .invoke(s, "documento.pdf").toString();
        assertThat(ext).isEqualTo(".pdf");
    }
}