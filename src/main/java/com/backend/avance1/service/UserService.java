package com.backend.avance1.service;

import com.backend.avance1.entity.RoleName;
import com.backend.avance1.entity.User;
import com.backend.avance1.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

@Service
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registrar(User user) {
        Preconditions.checkNotNull(user, "El usuario no puede ser nulo");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getEmail()), "El email es obligatorio");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getPassword()), "La contraseña es obligatoria");

        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> { throw new RuntimeException("El correo ya está registrado."); });

        userRepository.findByCelular(user.getCelular())
                .ifPresent(u -> { throw new RuntimeException("El celular ya está registrado."); });

        userRepository.findByDniRuc(user.getDniRuc())
                .ifPresent(u -> { throw new RuntimeException("El DNI/RUC ya está registrado."); });

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setFechaRegistro(LocalDateTime.now());

        Set<RoleName> roles = user.getRoles();
        if (roles == null || roles.isEmpty()) {
            roles = Sets.newHashSet(RoleName.ROLE_CLIENTE);
        }

        user.setRoles(roles);
        return userRepository.save(user);
    }

    public void promoverAConductor(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Preconditions.checkArgument(user.isActivo(), "El usuario debe tener la cuenta activa para ser promovido.");
        Preconditions.checkArgument(user.getRoles().contains(RoleName.ROLE_CLIENTE),
                "Solo los usuarios CLIENTE pueden solicitar ser CONDUCTOR.");

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
        Preconditions.checkNotNull(user, "El usuario no puede ser nulo");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(nuevaPassword), "La nueva contraseña no puede estar vacía");

        user.setPassword(passwordEncoder.encode(nuevaPassword));
        userRepository.save(user);
    }

    public User actualizarUsuario(User user) {
        Preconditions.checkNotNull(user, "El usuario no puede ser nulo");
        return userRepository.save(user);
    }

    public List<User> listarClientes() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(RoleName.ROLE_CLIENTE)
                        && !u.getRoles().contains(RoleName.ROLE_CONDUCTOR)
                        && !u.getRoles().contains(RoleName.ROLE_ADMIN)
                        && !u.getRoles().contains(RoleName.ROLE_SUPERADMIN))
                .collect(Collectors.toList());
    }

    public List<User> listarConductoresYClientes() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(RoleName.ROLE_CONDUCTOR)
                        && u.getRoles().contains(RoleName.ROLE_CLIENTE))
                .collect(Collectors.toList());
    }

    public List<User> listarAdminsYSuperAdmins() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(RoleName.ROLE_ADMIN)
                        || u.getRoles().contains(RoleName.ROLE_SUPERADMIN))
                .collect(Collectors.toList());
    }

    public Optional<User> buscarPorId(Long id) {
        return userRepository.findById(id);
    }
}