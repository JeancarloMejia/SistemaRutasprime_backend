package com.backend.avance1.service;

import com.backend.avance1.entity.RoleName;
import com.backend.avance1.entity.User;
import com.backend.avance1.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest extends BaseServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @Test
    void registrar_DeberiaGuardarUsuario_CuandoNoExistePrevio() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setCelular("999999999");
        user.setDniRuc("12345678");
        user.setPassword("123");

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.findByCelular(any())).thenReturn(Optional.empty());
        when(userRepository.findByDniRuc(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        User result = userService.registrar(user);

        assertThat(result).isNotNull();
        assertThat(result.getRoles()).contains(RoleName.ROLE_CLIENTE);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registrar_DeberiaFallar_CuandoEmailExiste() {
        User user = new User();
        user.setEmail("mail@test.com");
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.registrar(user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("El correo ya está registrado.");
    }

    @Test
    void promoverAConductor_DeberiaAgregarRolConductor() {
        User user = new User();
        user.setEmail("x@test.com");
        user.setActivo(true);
        user.setRoles(new HashSet<>(Set.of(RoleName.ROLE_CLIENTE)));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        userService.promoverAConductor(user.getEmail());

        assertThat(user.getRoles()).contains(RoleName.ROLE_CONDUCTOR);
        verify(userRepository).save(user);
    }
}