package com.example.gastosapp.service;

import com.example.gastosapp.dto.AuthResponse;
import com.example.gastosapp.model.User;
import com.example.gastosapp.model.RevokedToken;
import com.example.gastosapp.repository.UserRepository;
import com.example.gastosapp.repository.RevokedTokenRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class SupabaseAuthService {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseAuthService.class);

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseKey;

    @Value("${app.jwt.secret}")
    private String jwtSecret; // Para validar localmente el token de Supabase

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RevokedTokenRepository revokedTokenRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RestTemplate restTemplate; // ✅ Inyectado con timeout configurado

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Login o registro usando un token de Supabase.
     * El frontend debe enviar el access_token de Supabase tras el OAuth.
     * No el de Google.
     */
    public AuthResponse loginWithSupabase(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "accessToken is required");
        }

        // ✅ 1. Intentar validar el token localmente antes de llamar a Supabase
        String supabaseUserId = getUserIdFromToken(accessToken);
        if (supabaseUserId == null) {
            logger.info("Token inválido localmente, intentando validarlo con Supabase...");
        }

        // ✅ 2. Obtener datos del usuario desde Supabase
        JsonNode node = fetchSupabaseUser(accessToken);

        String id = node.path("id").asText(null);
        String email = node.path("email").asText(null);
        String name = node.path("user_metadata").path("full_name").asText(null);
        if (name == null || name.isBlank()) {
            name = node.path("user_metadata").path("name").asText(email);
        }

        if (email == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email not available in Supabase user");
        }

        // ✅ 3. Buscar o crear usuario local
        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setId(id);
            newUser.setCreatedAt(OffsetDateTime.now());
            return userRepository.save(newUser);
        });

        // Actualizar campos si es necesario
        if (user.getId() == null && id != null) {
            user.setId(id);
        }
        userRepository.save(user);

        // ✅ 4. Generar token JWT local
        String jwt = jwtService.generateToken(
                user.getId(),
                Map.of("email", user.getEmail(), "name", user.getName())
        );

        logger.info("Usuario autenticado correctamente: {}", email);

        return new AuthResponse(jwt, user);
    }

    /**
     * Revoca un token JWT para invalidarlo.
     */
    public void revokeToken(String token) {
        if (token == null || token.isBlank()) return;

        if (!revokedTokenRepository.existsByToken(token)) {
            revokedTokenRepository.save(new RevokedToken(token));
            logger.info("Token revocado correctamente.");
        }
    }

    /**
     * Verifica si un token fue revocado.
     */
    public boolean isTokenRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    /**
     * Obtiene información del usuario desde Supabase.
     */
    public Map<String, Object> getUserInfo(String accessToken) {
        JsonNode node = fetchSupabaseUser(accessToken);
        return mapper.convertValue(node, Map.class);
    }

    /**
     * Llama al endpoint de Supabase para obtener datos del usuario.
     */
    private JsonNode fetchSupabaseUser(String accessToken) {
        String url = supabaseUrl + "/auth/v1/user";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Supabase token");
            }
            return mapper.readTree(response.getBody());
        } catch (Exception e) {
            logger.error("Error al obtener usuario desde Supabase", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error fetching Supabase user info");
        }
    }

    /**
     * Valida localmente un token de Supabase para extraer el ID del usuario (claim 'sub').
     */
    private String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject();
        } catch (Exception e) {
            logger.debug("No se pudo validar el token localmente: {}", e.getMessage());
            return null;
        }
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
