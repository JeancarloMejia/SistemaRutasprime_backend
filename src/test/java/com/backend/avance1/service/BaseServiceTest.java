package com.backend.avance1.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

public abstract class BaseServiceTest {

    private AutoCloseable closeable;

    @BeforeEach
    void setUpMocks() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDownMocks() throws Exception {
        closeable.close();
    }
}