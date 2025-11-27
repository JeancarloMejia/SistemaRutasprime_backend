package com.backend.avance1.service;

import com.backend.avance1.dto.EmpresaRegistroDTO;
import com.backend.avance1.dto.UserDTO;

public interface DniValidatorServiceInterface {

    boolean validarDatosReniecUsuario(UserDTO userDto);

    boolean validarDatosReniecEmpresa(EmpresaRegistroDTO empresaDto);
}