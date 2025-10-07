package com.example.gastosapp.controller;

import com.example.gastosapp.service.SupabaseAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        try {
            // Obtener token del encabezado
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(401).body("Falta el token de autorización");
            }

            String token = authHeader.substring(7);

            // Obtener datos del usuario desde Supabase
            var userInfo = supabaseAuthService.getUserInfo(token);

            if (userInfo == null) {
                return ResponseEntity.status(404).body("Usuario no encontrado");
            }

            return ResponseEntity.ok(userInfo);

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error obteniendo el usuario: " + e.getMessage());
        }
    }
}
