package com.backend.avance1.config;

import com.backend.avance1.security.JwtAuthEntryPoint;
import com.backend.avance1.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/prometheus").permitAll()
                        // Endpoints públicos
                        .requestMatchers(
                                "/api/auth/public/**",
                                "/api/contact",
                                "/api/contact/",
                                "/api/archivos/**",
                                "/api/empresa/register",
                                "/api/empresa/login",
                                "/api/pagos/**",
                                "/api/viajes/activo",          // NUEVO: Obtener viaje activo (público)
                                "/api/viajes/historial",       // NUEVO: Historial de viajes (público)
                                "/api/viajes/{id}",            // NUEVO: Obtener viaje por ID (público)
                                "/api/viajes/{id}/cancelar",   // NUEVO: Cancelar viaje (público)
                                "/api/viajes/{id}/estado",      // NUEVO: Actualizar estado (público)
                                "/api/driver/available-trips",
                                "/api/driver/accept-trip",
                                "/api/driver/active-trip",
                                "/api/driver/update-status"
                        ).permitAll()

                        .requestMatchers("/api/auth/admin/login").permitAll()
                        .requestMatchers("/api/auth/admin/**").hasRole("SUPERADMIN")

                        .requestMatchers(
                                "/api/contact/reply",
                                "/api/contact/all",
                                "/api/contact/{code}"
                        ).hasAnyRole("ADMIN", "SUPERADMIN")

                        .requestMatchers("/api/conductor/apply", "/api/conductor/status")
                        .hasRole("CLIENTE")

                        .requestMatchers(
                                "/api/conductor/verify/**",
                                "/api/conductor/historial/**",
                                "/api/conductor/list"
                        ).hasAnyRole("ADMIN", "SUPERADMIN")

                        .requestMatchers(
                                "/api/empresa/verify/**",
                                "/api/empresa/list",
                                "/api/empresa/{id}",
                                "/api/empresa/historial/**",
                                "/api/empresa/update/**",
                                "/api/empresa/delete/**",
                                "/api/empresa/profile"
                        ).hasAnyRole("ADMIN", "SUPERADMIN", "CLIENTE")

                        .requestMatchers("/api/user/profile", "/api/user/update", "/api/user/change-password")
                        .authenticated()

                        .requestMatchers(
                                "/api/user/clientes",
                                "/api/user/conductores-clientes",
                                "/api/user/{id}",
                                "/api/user/export/excel",
                                "/api/user/delete/**"
                        ).hasAnyRole("ADMIN", "SUPERADMIN")

                        .requestMatchers("/api/user/admins")
                        .hasRole("SUPERADMIN")

                        // Endpoints de viajes con autenticación (si decides usarlos después)
                        .requestMatchers(
                                "/api/viajes/solicitar",
                                "/api/viajes/cliente/activo",
                                "/api/viajes/cliente/historial"
                        ).authenticated()

                        .requestMatchers(
                                "/api/viajes/conductor/disponible",
                                "/api/viajes/conductor/historial"
                        ).hasRole("CONDUCTOR")

                        .requestMatchers("/api/viajes/admin/todos")
                        .hasAnyRole("ADMIN", "SUPERADMIN")

                        .requestMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/super/**").hasRole("SUPERADMIN")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://tudominio.com"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}