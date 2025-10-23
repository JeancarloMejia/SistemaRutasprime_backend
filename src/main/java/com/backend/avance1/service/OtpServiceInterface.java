package com.backend.avance1.service;

public interface OtpServiceInterface {
    String generarOtp(String email);
    boolean validarOtp(String email, String codigo);
}