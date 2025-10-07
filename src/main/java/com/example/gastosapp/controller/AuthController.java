package com.example.gastosapp.controller;

import com.example.gastosapp.dto.AuthResponse;
import com.example.gastosapp.service.SupabaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    @PostMapping("/google")
    public ResponseEntity<?> loginWithSupabase(@RequestBody Map<String, String> body) {
        String accessToken = body != null ? body.get("accessToken") : null;
        AuthResponse resp = supabaseAuthService.loginWithSupabase(accessToken);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            supabaseAuthService.revokeToken(token);
        }
        return ResponseEntity.ok(Map.of("message", "logged out"));
    }
}
