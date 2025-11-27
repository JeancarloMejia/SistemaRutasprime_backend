package com.backend.avance1.security;

import com.backend.avance1.entity.Empresa;
import com.backend.avance1.entity.User;
import com.backend.avance1.service.EmpresaService;
import com.backend.avance1.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final EmpresaService empresaService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);
        String tipo = jwtUtil.extractTipo(token);

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if ("empresa".equals(tipo)) {
                authenticateEmpresa(email, token, request);
            } else {
                authenticateUser(email, token, request);
            }
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateEmpresa(String email, String token, HttpServletRequest request) {
        Optional<Empresa> empresaOpt = empresaService.buscarPorEmail(email);
        if (empresaOpt.isPresent() && jwtUtil.isTokenValid(token, email)) {
            Empresa empresa = empresaOpt.get();
            List<SimpleGrantedAuthority> authorities = extractAuthorities(token);
            setAuthentication(empresa, authorities, request);
        }
    }

    private void authenticateUser(String email, String token, HttpServletRequest request) {
        Optional<User> userOpt = userService.buscarPorEmail(email);
        if (userOpt.isPresent() && jwtUtil.isTokenValid(token, email)) {
            User user = userOpt.get();
            List<SimpleGrantedAuthority> authorities = extractAuthorities(token);
            setAuthentication(user, authorities, request);
        }
    }

    private List<SimpleGrantedAuthority> extractAuthorities(String token) {
        return jwtUtil.extractRoles(token).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private void setAuthentication(Object principal, List<SimpleGrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(principal, null, authorities);
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }
}