package com.example.gastosapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    // ⚠️ Debe coincidir con el "JWT secret" de tu proyecto Supabase
    @Value("${SUPABASE_JWT_SECRET}")
    private String secretKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // ✅ Validar token y extraer el claim "sub" (el userId del usuario de Supabase)
                String userId = getUserIdFromToken(token);

                if (userId == null || userId.isBlank()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token: missing user ID");
                    return;
                }

                // ✅ Agregar el userId como atributo al request
                request.setAttribute("userId", userId);

            } catch (io.jsonwebtoken.ExpiredJwtException ex) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            } catch (io.jsonwebtoken.SignatureException ex) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token signature");
                return;
            } catch (IOException ex) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or malformed token");
                return;
            }
        }

        // ✅ Si no hay token o es válido, continuar el flujo normal
        filterChain.doFilter(request, response);
    }

    private String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Supabase guarda el id del usuario en el claim "sub"
        return claims.getSubject();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
