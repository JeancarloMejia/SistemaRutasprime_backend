package com.backend.avance1.service;

import com.backend.avance1.entity.User;

import java.util.Optional;

public interface UserServiceInterface {

    User registrar(User user);

    void promoverAConductor(String email);

    Optional<User> buscarPorEmail(String email);

    void activarUsuario(User user);

    void actualizarPassword(User user, String nuevaPassword);

    User actualizarUsuario(User user);
}