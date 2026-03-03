package by.bsuir.semesterpassport.web.controller;

import by.bsuir.semesterpassport.application.dto.AuthResponse;
import by.bsuir.semesterpassport.application.dto.LoginRequest;
import by.bsuir.semesterpassport.application.service.AuthenticationService;
import by.bsuir.semesterpassport.application.service.JwtService;
import by.bsuir.semesterpassport.domain.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authService;
    private final JwtService jwtService;

    // Внедрение через конструктор (без Lombok)
    public AuthController(AuthenticationService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return authService.authenticate(request.email(), request.password())
                .map(user -> {
                    String token = jwtService.generateToken(user);
                    return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getRole().name()));
                })
                .orElse(ResponseEntity.status(401).build());
    }
}