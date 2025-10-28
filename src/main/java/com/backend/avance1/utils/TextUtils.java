package com.backend.avance1.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.Normalizer;

public class TextUtils {


    public static String normalizarTexto(String texto) {
        if (StringUtils.isBlank(texto)) {
            return "";
        }

        String normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        normalizado = StringUtils.trimToEmpty(normalizado).toUpperCase();
        normalizado = normalizado.replaceAll("\\s+", " ");

        return normalizado;
    }

}