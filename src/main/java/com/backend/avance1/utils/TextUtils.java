package com.backend.avance1.utils;

import java.text.Normalizer;

public class TextUtils {

    public static String normalizarTexto(String texto) {
        if (texto == null) return "";

        texto = Normalizer.normalize(texto, Normalizer.Form.NFD);

        texto = texto.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        texto = texto.toUpperCase().trim();

        texto = texto.replaceAll("\\s+", " ");

        return texto;
    }
}