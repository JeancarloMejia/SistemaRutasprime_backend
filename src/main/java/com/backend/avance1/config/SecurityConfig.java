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
                                "/api/contact/**",
                                "/api/archivos/**",
                                "/api/empresa/register",
                                "/api/empresa/login",
                                "/api/empresa/profile"
                        ).permitAll()

                        // Auth admin
                        .requestMatchers("/api/auth/admin/login").permitAll()
                        .requestMatchers("/api/auth/admin/**").hasRole("SUPERADMIN")

                        // Contact
                        .requestMatchers(
                                "/api/contact/reply",
                                "/api/contact/all",
                                "/api/contact/{code}"
                        ).hasAnyRole("ADMIN", "SUPERADMIN")

                        // Conductor - Cliente
                        .requestMatchers("/api/conductor/apply", "/api/conductor/status")
                        .hasRole("CLIENTE")

                        // Conductor - Admin
                        .requestMatchers(
                                "/api/conductor/verify/**",
                                "/api/conductor/historial/**",
                                "/api/conductor/list"
                        ).hasAnyRole("ADMIN", "SUPERADMIN")

                        // Empresa - Admin
                        .requestMatchers(
                                "/api/empresa/verify/**",
                                "/api/empresa/list",
                                "/api/empresa/{id}",
                                "/api/empresa/historial/**"
                        ).hasAnyRole("ADMIN", "SUPERADMIN")

                        // User
                        .requestMatchers("/api/user/profile", "/api/user/update", "/api/user/change-password")
                        .authenticated()
                        .requestMatchers(
                                "/api/user/clientes",
                                "/api/user/conductores-clientes",
                                "/api/user/{id}",
                                "/api/user/export/excel"
                        ).hasAnyRole("ADMIN", "SUPERADMIN")
                        .requestMatchers("/api/user/admins")
                        .hasRole("SUPERADMIN")

                        // Admin y Super
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