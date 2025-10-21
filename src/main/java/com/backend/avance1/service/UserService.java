package com.backend.avance1.service;

import com.backend.avance1.entity.RoleName;
import com.backend.avance1.entity.User;
import com.backend.avance1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registrar(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado.");
        }
        if (userRepository.findByCelular(user.getCelular()).isPresent()) {
            throw new RuntimeException("El celular ya está registrado.");
        }
        if (userRepository.findByDniRuc(user.getDniRuc()).isPresent()) {
            throw new RuntimeException("El DNI/RUC ya está registrado.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setFechaRegistro(LocalDateTime.now());

        Set<RoleName> roles = user.getRoles();
        if (roles == null) {
            roles = new HashSet<>();
        }

        roles.add(RoleName.ROLE_CLIENTE);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public void promoverAConductor(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.isActivo()) {
            throw new RuntimeException("El usuario debe tener la cuenta activa para ser promovido.");
        }

        if (!user.getRoles().contains(RoleName.ROLE_CLIENTE)) {
            throw new RuntimeException("Solo los usuarios CLIENTE pueden solicitar ser CONDUCTOR.");
        }

        Set<RoleName> roles = user.getRoles();
        if (!roles.contains(RoleName.ROLE_CONDUCTOR)) {
            roles.add(RoleName.ROLE_CONDUCTOR);
            user.setRoles(roles);
            userRepository.save(user);
        }
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