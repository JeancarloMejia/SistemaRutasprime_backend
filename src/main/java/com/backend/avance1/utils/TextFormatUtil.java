package com.backend.avance1.utils;

import org.apache.commons.lang3.StringUtils;

public class TextFormatUtil {

    private static final String DIGITOS = "0123456789";

    public static String capitalizarTexto(String texto) {
        if (StringUtils.isBlank(texto)) {
            return texto;
        }

        texto = StringUtils.normalizeSpace(texto.toLowerCase());
        String[] palabras = StringUtils.split(texto, ' ');

        for (int i = 0; i < palabras.length; i++) {
            palabras[i] = StringUtils.capitalize(palabras[i]);
        }

        return String.join(" ", palabras);
    }

    public static String capitalizarDireccion(String direccion) {
        if (StringUtils.isBlank(direccion)) {
            return direccion;
        }

        direccion = StringUtils.normalizeSpace(direccion.toLowerCase());
        String[] palabras = StringUtils.split(direccion, ' ');

        for (int i = 0; i < palabras.length; i++) {
            if (!StringUtils.containsAny(palabras[i], DIGITOS)) {
                palabras[i] = StringUtils.capitalize(palabras[i]);
            }
        }

        return String.join(" ", palabras);
    }
}