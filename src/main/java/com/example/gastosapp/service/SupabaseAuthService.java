package com.example.gastosapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

@Service
public class SupabaseAuthService {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseAuthService.class);

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.anon-key}")
    private String supabaseKey;

    @Autowired
    private RestTemplate restTemplate; // ✅ Inyectado con timeout configurado

    private final ObjectMapper mapper = new ObjectMapper();

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

}
