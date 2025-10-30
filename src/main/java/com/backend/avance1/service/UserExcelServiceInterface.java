package com.backend.avance1.service;

import java.io.IOException;

public interface UserExcelServiceInterface {

    byte[] exportUsersToExcel() throws IOException;
}