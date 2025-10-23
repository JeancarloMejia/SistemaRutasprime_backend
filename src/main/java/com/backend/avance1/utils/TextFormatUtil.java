package com.backend.avance1.utils;

public class TextFormatUtil {

    public static String capitalizarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return texto;
        }

        texto = texto.toLowerCase().trim();

        StringBuilder resultado = new StringBuilder();
        for (String palabra : texto.split("\\s+")) {
            if (!palabra.isEmpty()) {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }
        return resultado.toString().trim();
    }

    public static String capitalizarDireccion(String direccion) {
        if (direccion == null || direccion.trim().isEmpty()) {
            return direccion;
        }

        direccion = direccion.toLowerCase().trim();

        StringBuilder resultado = new StringBuilder();
        for (String palabra : direccion.split("\\s+")) {
            if (palabra.matches(".*\\d.*")) {
                resultado.append(palabra).append(" ");
            } else {
                resultado.append(Character.toUpperCase(palabra.charAt(0)))
                        .append(palabra.substring(1))
                        .append(" ");
            }
        }
        return resultado.toString().trim();
    }
}