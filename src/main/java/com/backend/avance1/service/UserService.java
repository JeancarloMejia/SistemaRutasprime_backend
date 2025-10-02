package com.backend.avance1.service;

import com.backend.avance1.entity.User;
import com.backend.avance1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registrar(User user) {
        // Validaciones de unicidad
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        if (userRepository.findByCelular(user.getCelular()).isPresent()) {
            throw new RuntimeException("El celular ya está registrado.");
        }
        if (userRepository.findByDniRuc(user.getDniRuc()).isPresent()) {
            throw new RuntimeException("El DNI/RUC ya está registrado.");
        }

        // Encriptar contraseña y guardar
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> buscarPorEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void activarUsuario(User user) {
        user.setActivo(true);
        userRepository.save(user);
    }

    public void actualizarPassword(User user, String nuevaPassword) {
        user.setPassword(passwordEncoder.encode(nuevaPassword));
        userRepository.save(user);
    }

    public User actualizarUsuario(User user) {
        return userRepository.save(user);
    }
}
