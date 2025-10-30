package com.backend.avance1.service;

import com.backend.avance1.dto.UserDTO;

public interface DniValidatorServiceInterface {

    boolean validarDatosReniec(UserDTO userDto);
}