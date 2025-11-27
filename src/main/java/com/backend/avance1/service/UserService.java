package com.backend.avance1.service;

import com.backend.avance1.entity.RoleName;
import com.backend.avance1.entity.User;
import com.backend.avance1.repository.EmpresaRepository;
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
    private final EmpresaRepository empresaRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public User registrar(User user) {
        Preconditions.checkNotNull(user, "El usuario no puede ser nulo");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getEmail()), "El email es obligatorio");
        Preconditions.checkArgument(!Strings.isNullOrEmpty(user.getPassword()), "La contraseña es obligatoria");

        userRepository.findByEmail(user.getEmail())
                .ifPresent(u -> { throw new RuntimeException("El correo ya está registrado."); });

        empresaRepository.findByCorreoCorporativo(user.getEmail())
                .ifPresent(e -> { throw new RuntimeException("El correo ya está en uso por una empresa."); });

        userRepository.findByCelular(user.getCelular())
                .ifPresent(u -> { throw new RuntimeException("El celular ya está registrado."); });

        empresaRepository.findByTelefono(user.getCelular())
                .ifPresent(e -> { throw new RuntimeException("El celular ya está en uso por una empresa."); });

        userRepository.findByDniRuc(user.getDniRuc())
                .ifPresent(u -> { throw new RuntimeException("El DNI/RUC ya está registrado."); });

        empresaRepository.findByDni(user.getDniRuc())
                .ifPresent(e -> { throw new RuntimeException("El DNI ya está en uso por una empresa."); });

        empresaRepository.findByRucEmpresa(user.getDniRuc())
                .ifPresent(e -> { throw new RuntimeException("El RUC ya está en uso por una empresa."); });

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

    public Optional<User> buscarPorCelular(String celular) {
        return userRepository.findByCelular(celular);
    }

    public Optional<User> buscarPorDniRuc(String dniRuc) {
        return userRepository.findByDniRuc(dniRuc);
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

    public boolean celularDisponibleParaUsuario(String celular, Long userId) {
        return userRepository.findByCelular(celular)
                .map(existingUser -> existingUser.getId().equals(userId))
                .orElse(true);
    }

    public long contarUsuariosClientes() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(RoleName.ROLE_CLIENTE)
                        && !u.getRoles().contains(RoleName.ROLE_CONDUCTOR)
                        && !u.getRoles().contains(RoleName.ROLE_ADMIN)
                        && !u.getRoles().contains(RoleName.ROLE_SUPERADMIN))
                .count();
    }

    public long contarUsuariosClientesYConductores() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(RoleName.ROLE_CLIENTE)
                        && u.getRoles().contains(RoleName.ROLE_CONDUCTOR))
                .count();
    }

    public long contarUsuariosAdminsYSuperAdmins() {
        return userRepository.findAll().stream()
                .filter(u -> u.getRoles().contains(RoleName.ROLE_ADMIN)
                        || u.getRoles().contains(RoleName.ROLE_SUPERADMIN))
                .count();
    }

    public boolean eliminarUsuario(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }
}